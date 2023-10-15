package utils.auth

import com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler
import play.api.libs.json.JsString
import play.api.mvc
import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.Future

class CustomUnsecuredErrorHandler extends UnsecuredErrorHandler {

  override def onNotAuthorized(implicit request: RequestHeader): Future[mvc.Result] = {
    Future.successful(Forbidden(JsString("Your role have not permission to perform this operation")))
  }

}
