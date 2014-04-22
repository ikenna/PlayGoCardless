package controllers

import play.api.mvc._
import services.{ErrorConfirmation, OKConfirmation, DirectDebitService, GoCardlessService}

object Application extends Controller {

  val directDebitService: DirectDebitService = GoCardlessService

  def index = Action { implicit request =>
    Ok(views.html.index(directDebitService.getSubscriptionUrl))
  }

  def confirm = Action {implicit request =>
    directDebitService.confirm match {
      case OKConfirmation => Ok("Thank you for setting up a direct debit!")
      case ErrorConfirmation => InternalServerError("Sorry, something went wrong")
    }
  }
}












