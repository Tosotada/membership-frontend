package services

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.{Json, Reads}
import play.api.libs.ws.{Response, WS}

import model.Stripe._
import configuration.Config

trait StripeService {
  protected val apiURL: String
  protected val apiSecret: String

  implicit val readsError = Json.reads[Error]
  implicit val readsCard = Json.reads[Card]
  implicit val readsCardList = Json.reads[CardList]
  implicit val readsCharge = Json.reads[Charge]
  implicit val readsPlan = Json.reads[Plan]
  implicit val readsSubscription = Json.reads[Subscription]
  implicit val readsSubscriptionList = Json.reads[SubscriptionList]
  implicit val readsCustomer = Json.reads[Customer]

  private def headers = ("Authorization", s"Bearer $apiSecret")


  private def extract[A <: StripeObject](response: Response)(implicit reads: Reads[A]): A = {
    response.json.asOpt[A].getOrElse {
      throw (response.json \ "error").asOpt[Error].getOrElse(Error("internal", "Unable to extract object"))
    }
  }

  def post[A <: StripeObject](endpoint: String, data: Map[String, Seq[String]])(implicit reads: Reads[A]): Future[A] = {
    httpPost(s"$apiURL/$endpoint", data, headers).map(extract[A])
  }

  def get[A <: StripeObject](endpoint: String)(implicit reads: Reads[A]): Future[A] = {
    httpGet(s"$apiURL/$endpoint", headers).map(extract[A])
  }

  def createCharge(amount: Int, currency: String, card: String, description: String): Future[Charge] = {
    post[Charge]("charges", Map(
      "amount" -> Seq(amount.toString),
      "currency" -> Seq(currency),
      "card" -> Seq(card),
      "description" -> Seq(description)
    ))
  }

  def createCustomer(card: String): Future[Customer] = {
    post[Customer]("customers", Map("card" -> Seq(card)))
  }

  def readCustomer(customerId: String): Future[Customer] =
    get[Customer](s"customers/$customerId")

  def createSubscription(customerId: String, planId: String): Future[Subscription] = {
    post[Subscription](s"customers/$customerId/subscriptions", Map("plan" -> Seq(planId)))
  }

  def httpPost(url: String, data: Map[String, Seq[String]], header: (String, String)*): Future[Response] = {
    WS.url(url).withHeaders(header: _*).post(data)
  }

  def httpGet(url: String, header: (String, String)*): Future[Response] = {
    WS.url(url).withHeaders(header: _*).get
  }
}

object StripeService extends StripeService {
  protected val apiURL = Config.stripeApiURL
  protected val apiSecret = Config.stripeApiSecret
}
