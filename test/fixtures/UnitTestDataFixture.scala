package fixtures

import com.mohiva.play.silhouette.password.BCryptSha256PasswordHasher
import domain.dao.{OrderDao, OrderDetailDao, ProductDao, UserDao}
import domain.models.{Order, OrderDetail, Product, User}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.reflect.ClassTag

abstract class UnitTestDataFixture extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar with BeforeAndAfterAll with ScalaFutures {

  def get[T: ClassTag]: T = app.injector.instanceOf[T]

  val productDao: ProductDao = get[ProductDao]
  val userDao: UserDao = get[UserDao]
  val orderDao: OrderDao = get[OrderDao]
  val orderDetailDao: OrderDetailDao = get[OrderDetailDao]

  case class LoginBody(email: String, password: String)
  implicit val formatLoginBody: OFormat[LoginBody] = Json.format[LoginBody]

  override protected def beforeAll(): Unit = {
    val existUser = Await.result(userDao.findByEmail(Users.email), 5.seconds)
    if (existUser.isEmpty) {
      Await.result(userDao.save(Users.admin), 5.seconds)
    }
  }

  object Products {
    val productEntity: Product = Product(Some(1L), "Product 1", 25, LocalDateTime.now())
    val productEntity2: Product = Product(Some(2L), "Product 2", 50, LocalDateTime.now())

    val allProducts: Seq[Product] = Seq(productEntity, productEntity2)
  }

  object Users {
    val password: String = new BCryptSha256PasswordHasher().hash("123456").password
    val email = "admin@gmail.com"

    val loginBody: LoginBody = LoginBody(email, "123456")
    val invalidLoginBody: LoginBody = LoginBody("invalid", "123456")

    val admin: User = User(Some(1L), email, "Admin", "X", password, "Admin", LocalDateTime.now())
    val operator: User = User(Some(2L), "operator@gmail.com", "Operator", "Y", password , "Operator", LocalDateTime.now())
    val user: User = User(Some(3L), "user@gmail.com", "User", "Z", password , "User", LocalDateTime.now())

    val allUsers: Seq[User] = Seq(operator, user)
  }

  object Orders {
    val orderEntity: Order = Order(Some(1L), 1L, LocalDateTime.now(), 100)
    val orderEntity2: Order = Order(Some(2L), 1L, LocalDateTime.now(), 100)

    val allOrders: Seq[Order] = Seq(orderEntity, orderEntity2)
  }

  object OrderDetails {
    val orderDetailEntity: OrderDetail = OrderDetail(Some(1L), 1L, 1L, 50, 2)
    val allOrderDetails: Seq[OrderDetail] = Seq(orderDetailEntity)
  }

}