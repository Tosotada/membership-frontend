package services

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.agent.Agent

import com.gu.membership.salesforce.Tier

import play.api.Logger
import play.api.Play.current
import play.api.libs.concurrent.Akka

import configuration.Config
import forms.MemberForm.{NameForm, AddressForm}
import model.Zuora._
import model.Stripe

case class SubscriptionServiceError(s: String) extends Throwable {
  override def getMessage: String = s
}

trait SubscriptionService {
  val zuora: ZuoraService

  val friendPlan = "2c92c0f945fee1c90146057402c7066b"

  case class PaidPlan(monthly: String, annual: String)

  object PaidPlan {
    val plans = Map(
      Tier.Partner -> PaidPlan("2c92c0f945fee1c9014605749e450969", "2c92c0f8471e22bb01471ffe9596366c"),
      Tier.Patron -> PaidPlan("2c92c0f845fed48301460578277167c3", "2c92c0f9471e145d01471ffd7c304df9")
    )

    def apply(tier: Tier.Tier, annual: Boolean): String = {
      val plan = plans(tier)
      if (annual) plan.annual else plan.monthly
    }
  }

  /**
   * Zuora doesn't return fields which are empty! This method guarantees that the keys will
   * be in the map and also that the query only returns one result
   */
  def queryOne(fields: Seq[String], table: String, where: String): Future[Map[String, String]] = {
    val q = s"SELECT ${fields.mkString(",")} FROM $table WHERE $where"
    zuora.Query(q).mkRequest().map(Query(_)).map { case Query(results) =>
      if (results.length != 1) {
        throw new SubscriptionServiceError(s"Query $q returned more than one result")
      }

      fields.map { field => (field, results(0).getOrElse(field, "")) }.toMap
    }
  }

  def queryOne(field: String, table: String, where: String): Future[String] =
    queryOne(Seq(field), table, where).map(_(field))

  def getBasicIds(sfAccountId: String): Future[(String, String)] = {
    for {
      accountId <- queryOne("Id", "Account", s"crmId='$sfAccountId'")
      subscriptionId <- queryOne("Id", "Subscription", s"AccountId='$accountId' AND Status='Active'")
    } yield (accountId, subscriptionId)
  }

  def createPaidSubscription(sfAccountId: String, customer: Stripe.Customer, tier: Tier.Tier,
                             annual: Boolean, name: NameForm, address: AddressForm): Future[Subscription] = {
    val plan = PaidPlan(tier, annual)
    zuora.Subscribe(sfAccountId, Some(customer), plan, name, address).mkRequest().map(Subscription(_))
  }

  def createFriendSubscription(sfAccountId: String, name: NameForm, address: AddressForm): Future[Subscription] = {
    zuora.Subscribe(sfAccountId, None, friendPlan, name, address).mkRequest().map(Subscription(_))
  }

  def getInvoiceSummary(sfAccountId: String): Future[InvoiceItem] = {
    val invoiceKeys = Seq("ServiceStartDate", "ServiceEndDate", "ProductName", "ChargeAmount", "TaxAmount")
    for {
      (accountId, subscriptionId) <- getBasicIds(sfAccountId)
      // When an upgrade happens, the user is refunded some money. At then moment we ignore negative invoices
      // because we can only upgrade from a friend
      // TODO: we will probably want to show the negative invoice item at some point
      invoice <- queryOne(invoiceKeys, "InvoiceItem", s"SubscriptionId='$subscriptionId' AND ChargeAmount>0")
    } yield InvoiceItem.fromMap(invoice)
  }

  def createPaymentMethod(sfAccountId: String, customer: Stripe.Customer): Future[String] = {
    for {
      accountId <- queryOne("Id", "Account", s"crmId='$sfAccountId'")
      paymentMethod <- zuora.CreatePaymentMethod(accountId, customer).mkRequest().map(PaymentMethod(_))
      _ <- zuora.SetDefaultPaymentMethod(accountId, paymentMethod.id).mkRequest()
    } yield accountId
  }

  def downgradeSubscription(sfAccountId: String, tier: Tier.Tier, annual: Boolean): Future[String] = {
    val newRatePlanId = tier match {
      case Tier.Friend => friendPlan
      case t => PaidPlan(t, annual)
    }

    for {
      (accountId, subscriptionId) <- getBasicIds(sfAccountId)
      ratePlanId <- queryOne("Id", "RatePlan", s"SubscriptionId='$subscriptionId'")
      chargedThroughDate <- queryOne("ChargedThroughDate", "RatePlanCharge", s"RatePlanId='$ratePlanId'")
      result <- zuora.DowngradePlan(subscriptionId, ratePlanId, newRatePlanId, new DateTime(chargedThroughDate)).mkRequest()
    } yield ""
  }

  def upgradeSubscription(sfAccountId: String, tier: Tier.Tier, annual: Boolean): Future[Subscription] = {
    val newRatePlanId = PaidPlan(tier, annual)

    for {
      (accountId, subscriptionId) <- getBasicIds(sfAccountId)
      ratePlanId  <- queryOne("Id", "RatePlan", s"SubscriptionId='$subscriptionId'")
      result <- zuora.UpgradePlan(subscriptionId, ratePlanId, newRatePlanId).mkRequest()
    } yield Subscription(result)
  }

}

object SubscriptionService extends SubscriptionService {
  val zuora = new ZuoraService {
    val apiUrl = Config.zuoraApiUrl
    val apiUsername = Config.zuoraApiUsername
    val apiPassword = Config.zuoraApiPassword

    def authentication: Authentication = authenticationAgent.get()
  }

  private implicit val system = Akka.system

  val authenticationAgent = Agent[Authentication](Authentication("", ""))

  def refresh() {
    Logger.debug("Refreshing Zuora login")
    authenticationAgent.sendOff(_ => {
      val auth = Await.result(zuora.Login().mkRequest().map(Authentication(_)), 15.seconds)
      Logger.debug(s"Got Zuora login $auth")
      auth
    })
  }

  def start() {
    Logger.info("Starting Zuora background tasks")
    system.scheduler.schedule(0.seconds, 2.hours) { refresh() }
  }
}
