package utils.auth

import javax.inject.Inject
import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import play.api
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.JsString
import play.api.mvc
import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.Future

class CustomSecuredErrorHandler @Inject()(val messagesApi: MessagesApi) extends SecuredErrorHandler with I18nSupport {

  override def onNotAuthenticated(implicit request: RequestHeader): Future[mvc.Result] = {
    Future.successful(Unauthorized(JsString("Authentication required to perform this operation")))
  }

  override def onNotAuthorized(implicit request: RequestHeader): Future[api.mvc.Result] = {
    Future.successful(Forbidden(JsString("Your role have not permission to perform this operation")))
  }

}
