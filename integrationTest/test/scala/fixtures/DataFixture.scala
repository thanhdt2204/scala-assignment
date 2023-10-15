package fixtures

import com.mohiva.play.silhouette.password.BCryptSha256PasswordHasher
import domain.dao.{ProductDao, UserDao}
import domain.models.User
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.reflect.ClassTag

abstract class DataFixture extends PlaySpec with GuiceOneAppPerSuite with BeforeAndAfterAll {

  def get[T : ClassTag]: T = app.injector.instanceOf[T]

  val productDao: ProductDao = get[ProductDao]
  val userDao: UserDao = get[UserDao]

  case class LoginBody(email: String, password: String)
  implicit val formatLoginBody: OFormat[LoginBody] = Json.format[LoginBody]

  case class UserBody(email: String, firstName: String, lastName: String, birthDate: String)
  implicit val formatUserBody: OFormat[UserBody] = Json.format[UserBody]

  case class ProductBody(productName: String, price: Long)
  implicit val formatProductBody: OFormat[ProductBody] = Json.format[ProductBody]

  case class UserOutput(id: Long, email: String, firstName: String, lastName: String, role: String, birthDate: LocalDateTime)
  implicit val formatUserOutput: OFormat[UserOutput] = Json.format[UserOutput]

  val authHeaderKey: String = "X-Auth"

  override protected def beforeAll(): Unit = {
    createAdmin(Users.admin)
  }

  def createAdmin(user: User): Unit = {
    val existUser = Await.result(userDao.findByEmail(user.email), 5.seconds)
    if (existUser.isEmpty) {
      Await.result(userDao.save(user), 5.seconds)
    }
  }

  object Users {
    val plainPassword: String = "123456"
    val password: String = new BCryptSha256PasswordHasher().hash(plainPassword).password
    val email: String = "admin@gmail.com"

    val loginBody: LoginBody = LoginBody(email, plainPassword)
    val loginInvalidBody: LoginBody = LoginBody("XXX", "YYY")

    val admin: User = User(None, email, "Admin", "X", password, "Admin", LocalDateTime.now())

    val userInput: UserBody = UserBody("operator@gmail.com", "Operator", "Z", "2023-10-11 22:41:11")

    val operator: User = User(None, "operator@gmail.com", "Operator", "Y", password, "Operator", LocalDateTime.now())
    val user: User = User(None, "user@gmail.com", "User", "Z", password, "User", LocalDateTime.now())
  }

  object Products {
    val productInput: ProductBody = ProductBody("Product 1", 25)
  }

}