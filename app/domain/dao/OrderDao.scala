package domain.dao

import domain.models.Order
import domain.tables.OrderTable
import exceptions.NotFoundException
import slick.jdbc.PostgresProfile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class OrderDao @Inject()(daoRunner: DaoRunner)(implicit ec: DbExecutionContext) {

  private val orders = TableQuery[OrderTable]

  def findAllByUserId(userId: Option[Long]): Future[Iterable[Order]] = daoRunner.run {
    if (userId.isEmpty) {
      orders.result
    } else {
      orders.filter(_.userId === userId.get).result
    }
  }

  def findById(id: Long): Future[Option[Order]] = daoRunner.run {
    orders.filter(_.id === id).result.headOption
  }

  def save(order: Order): Future[Order] = daoRunner.run {
    orders returning orders += order
  }

  def update(order: Order): Future[Order] = daoRunner.run {
    orders.filter(_.id === order.id).take(1).result.headOption.flatMap {
      case Some(_) =>
        orders.filter(_.id === order.id).update(order).map(_ => order)
      case None =>
          throw new NotFoundException("Order not found with id " + order.id.get)
    }
  }

  def delete(id: Long): Future[Int] = daoRunner.run {
    orders.filter(_.id === id).delete
  }

}
