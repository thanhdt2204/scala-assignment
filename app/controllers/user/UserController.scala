package controllers.user

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import domain.models.User
import exceptions.{BadRequestException, NotFoundException}
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc._
import services.UserService
import utils.auth.{JWTEnvironment, WithRole}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class UserFormInput(email: String, firstName: String, lastName: String, birthDate: LocalDateTime)

class UserController @Inject()(cc: ControllerComponents,
                               userService: UserService,
                               silhouette: Silhouette[JWTEnvironment],
                               passwordHasherRegistry: PasswordHasherRegistry)
                              (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

  private val form: Form[UserFormInput] = {
    import play.api.data.Forms._
    Form(
      mapping(
        "email" -> email,
        "firstName" -> nonEmptyText(maxLength = 64),
        "lastName" -> nonEmptyText(maxLength = 64),
        "birthDate" -> localDateTime
      )(UserFormInput.apply)(UserFormInput.unapply)
    )
  }

  def getAll: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      userService.findAll().map {
        users => {
          if (users.isEmpty)
            NoContent
          else
            Ok(Json.toJson(users))
        }
      }
    }

  def getById(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      userService.findById(id).map {
        case Some(user) => Ok(Json.toJson(user))
        case None => throw new NotFoundException("User not found with id " + id)
      }
    }

  def save: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request => processJson(None) }

  def update(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request => processJson(Some(id)) }

  def delete(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      userService.delete(id).map { deletedCnt =>
        if (deletedCnt == 1)
          Ok(JsString(s"Delete user id $id successfully"))
        else
          throw new BadRequestException("Unable to delete user " + id)
      }
    }

  private def processJson[A](id: Option[Long])(implicit request: Request[A]): Future[Result] = {

    def failure(badForm: Form[UserFormInput]): Nothing = {
      throw new BadRequestException(badForm.toString)
    }

    def success(input: UserFormInput): Future[Result] = {
      val authInfo = passwordHasherRegistry.current.hash("123456")
      val user = User(id, input.email, input.firstName, input.lastName, authInfo.password , "User", input.birthDate)
      id match {
        case None =>
          userService.save(user).map {
            user => Created(Json.toJson(user))
          }
        case Some(_) =>
          userService.update(user).map {
            user => Ok(Json.toJson(user))
          }
      }
    }

    form.bindFromRequest().fold(failure, success)
  }

}
