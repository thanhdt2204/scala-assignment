package domain.models

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.password.BCryptSha256PasswordHasher
import play.api.libs.json.{Json, Writes}

import java.time.LocalDateTime

case class User(id: Option[Long],
                email: String,
                firstName: String,
                lastName: String,
                password: String,
                role: String,
                birthDate: LocalDateTime,
                address: Option[String] = None,
                phoneNumber: Option[String] = None) extends Identity {

  def loginInfo = LoginInfo(CredentialsProvider.ID, email)

  def passwordInfo: PasswordInfo = PasswordInfo(BCryptSha256PasswordHasher.ID, password)

}

object User {
  implicit val format: Writes[User] = Writes { user =>
    Json.obj(
      "id" -> user.id,
      "email" -> user.email,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "role" -> user.role,
      "birthDate" -> user.birthDate
    )
  }
}

