package controllers.auth

import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Credentials
import play.api.i18n.Lang
import play.api.libs.json.{JsString, Json, OFormat}
import play.api.mvc.{Action, AnyContent, Request}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class SignInModel(email: String, password: String)

class SignInController @Inject()(components: SilhouetteControllerComponents)(implicit ex: ExecutionContext)
  extends SilhouetteController(components) {



  implicit val signInFormat: OFormat[SignInModel] = Json.format[SignInModel]

  def signIn: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    logger.info("Login user")
    implicit val lang: Lang = supportedLangs.availables.head

    request.body.asJson.flatMap(_.asOpt[SignInModel]) match {
      case Some(signInModel) =>
        val credentials = Credentials(signInModel.email, signInModel.password)

        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(_) =>
              for {
                authenticator <- authenticatorService.create(loginInfo)
                token <- authenticatorService.init(authenticator)
                result <- authenticatorService.embed(token, Ok)
              } yield {
                result
              }
            case None => Future.successful(BadRequest(JsString(messagesApi("could.not.find.user"))))
          }
        }.recover {
          case e: ProviderException => BadRequest(JsString(e.toString))
        }
      case None => Future.successful(BadRequest(JsString(messagesApi("could.not.find.user"))))
    }
  }

}
