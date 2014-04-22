package services

import play.api.mvc.{AnyContent, Request}
import java.math
import gocardless.connect.{Connect, Resource, Subscription}
import controllers.routes
import scala.util.{Failure, Success, Try}
import com.typesafe.config.ConfigFactory
import gocardless.{AccountDetails, GoCardless}
import play.api.Logger


object GoCardlessService extends DirectDebitService {

  val amount = new math.BigDecimal("15.0")
  val intervalLength = 1
  val intervalUnit = "month"


  override def getSubscriptionUrl(implicit request: Request[AnyContent]): String = {
    val subscription = new Subscription(ApiKeys.merchantId, amount, intervalLength, intervalUnit)
    val redirectUri:String = routes.Application.confirm().absoluteURL()
    Account.connect.newSubscriptionUrl(subscription, redirectUri, "", "")
  }

  override def confirm(implicit request: Request[AnyContent]): Confirmation = {
    val resource: Option[Resource] = for {
      resourceUri <- request.getQueryString("resource_uri")
      resourceId <- request.getQueryString("resource_id")
      resourceType <- request.getQueryString("resource_type")
      signature <- request.getQueryString("signature")
      state <- request.getQueryString("state")}
    yield {
      Logger.debug(s"Resource info -  $resourceUri, $resourceId, $resourceType, $signature")
      val r = new Resource()
      r.setResourceUri(resourceUri)
      r.setResourceId(resourceId)
      r.setResourceType(resourceType)
      r.setState(state)
      r.setSignature(signature)
      r
    }
    Try(resource map Account.connect.confirm) match {
      case Success(s) => OKConfirmation
      case Failure(t) => {
        Logger.error("Error!!", t)
        ErrorConfirmation
      }
    }
  }
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

object ApiKeys {
  val conf = ConfigFactory.systemEnvironment()
  val appIdentifier = conf.getString("GOCARDLESS_APPIDENTIFIER")
  val appSecret = conf.getString("GOCARDLESS_APPSECRET")
  val merchantAccessToken = conf.getString("GOCARDLESS_MERCHANTACCESSTOKEN")
  val merchantId = conf.getString("GOCARDLESS_MERCHANTID")
}
