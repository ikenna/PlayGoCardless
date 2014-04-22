package services

import play.api.mvc.{AnyContent, Request}

trait DirectDebitService {
  val amount: java.math.BigDecimal
  val intervalLength : Int
  val intervalUnit: String

  def getSubscriptionUrl(implicit request: Request[AnyContent]): String

  def confirm(implicit request: Request[AnyContent]): Confirmation
}


sealed trait Confirmation
case object OKConfirmation extends Confirmation
case object ErrorConfirmation extends Confirmation

