package utils.auth

import com.mohiva.play.silhouette.api.{Authenticator, Authorization}
import domain.dao.OrderDao
import domain.models.User
import exceptions.NotFoundException
import play.api.mvc.Request

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * Roles: "Admin" | "Operator" | "User"
 */
case class WithRole[A <: Authenticator](anyOf: String*) extends Authorization[User, A] {

  override def isAuthorized[B](user: User, authenticator: A)(implicit request: Request[B]): Future[Boolean] = {
    Future.successful(anyOf.contains(user.role))
  }

}

case class WithRoleOwnUser[A <: Authenticator](orderDao: OrderDao, id: Long)  extends Authorization[User, A] {

  override def isAuthorized[B](user: User, authenticator: A)(implicit request: Request[B]): Future[Boolean] = {
    val order = Await.result(orderDao.findById(id), Duration.Inf)
    if (order.isEmpty)
      throw new NotFoundException("Order not found with id " + id)
    Future.successful(order.get.userId == user.id.get)
  }

}