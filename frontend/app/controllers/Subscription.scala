package controllers

import scala.concurrent.Future

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.data._
import play.api.data.Forms._

import services.{ MemberService, StripeService }
import model.{ Stripe, Tier, Member }
import actions.{MemberAction, AuthenticatedAction, AuthRequest}

trait Subscription extends Controller {

  def subscribe = AuthenticatedAction.async { implicit request =>
    paymentForm.bindFromRequest
      .fold(_ => Future.successful(BadRequest), makePayment)
  }

  private val paymentForm =
    Form { tuple("stripeToken" -> nonEmptyText, "tier" -> nonEmptyText) }

  private def makePayment(formData: (String, String))(implicit request: AuthRequest[_]) = {
    val (stripeToken, tier) = formData
    val payment = for {
      customer <- StripeService.Customer.create(stripeToken)
      subscription <- StripeService.Subscription.create(customer.id, tier)
      member = Member(request.user.id, Tier.withName(tier), customer.id)
    } yield {
      MemberService.put(member)
      Ok(subscription.id)
    }

    payment.recover {
      case error: Stripe.Error => BadRequest(error.message)
    }
  }

  def cancel = MemberAction.async { implicit request =>
    for {
      customer <- StripeService.Customer.read(request.member.customerId)
      subscriptionId = customer.subscriptions.data.headOption.map(_.id).getOrElse("")
      subscription <- StripeService.Subscription.delete(customer.id, subscriptionId)
    } yield Ok
  }
}

object Subscription extends Subscription
