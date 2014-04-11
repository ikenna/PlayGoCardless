package controllers

import play.api.mvc._
import gocardless.{GoCardless, AccountDetails}
import gocardless.connect.{Resource, Connect, Subscription}
import java.math
import com.typesafe.config.ConfigFactory

object Application extends Controller {


  def index = Action {implicit request =>
    Ok(views.html.index(subscriptionUrl))
  }

  def confirm = Action { request =>
    val resource: Option[Resource] = for {
      resourceUri <- request.getQueryString("resource_uri")
      resourceId <- request.getQueryString("resource_id")
      resourceType <- request.getQueryString("resource_type")
      signature <- request.getQueryString("signature")}
    yield {
      val r = new Resource()
      r.setResourceUri(resourceUri)
      r.setResourceId(resourceId)
      r.setResourceType(resourceType)
      r.setSignature(signature)
      r
    }

    resource map Account.connect.confirm
    Ok("Your Direct Debit Subscription has been setup. Thank you for setting up a direct debit!")
  }


  def subscriptionUrl(implicit request: Request[AnyContent]): String = {
    val (amount, intervalLength, intervalUnit) = (new math.BigDecimal("15.0"), 1, "month")
    val subscription = new Subscription(ApiKeys.merchantId, amount, intervalLength, intervalUnit)
    val redirectUri = routes.Application.confirm().absoluteURL()
    Account.connect.newSubscriptionUrl(subscription, redirectUri, null, null)
  }
}


object ApiKeys {
  val conf = ConfigFactory.systemEnvironment()
  val appIdentifier = conf.getString("GOCARDLESS_APPIDENTIFIER")
  val appSecret = conf.getString("GOCARDLESS_APPSECRET")
  val merchantAccessToken = conf.getString("GOCARDLESS_MERCHANTACCESSTOKEN")
  val merchantId = conf.getString("GOCARDLESS_MERCHANTID")
}

object Account {
  GoCardless.environment = GoCardless.Environment.SANDBOX

  val accountDetails = new AccountDetails()
  accountDetails.setAppSecret(ApiKeys.appSecret)
  accountDetails.setAppId(ApiKeys.appIdentifier)
  accountDetails.setAccessToken(ApiKeys.merchantAccessToken)
  accountDetails.setMerchantId(ApiKeys.merchantId)

  val connect = new Connect(accountDetails)
}