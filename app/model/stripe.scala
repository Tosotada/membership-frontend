package model

package object stripe {

  trait StripeObject

  case class Error(`type`: String, message: String) extends Throwable with StripeObject

  case class Card(`type`: String, last4: String) extends StripeObject

  case class Charge(amount: Int, currency: String, card: Card, description: String)
    extends StripeObject

  case class Customer(id: String) extends StripeObject

  case class Subscription(id: String) extends StripeObject

}
