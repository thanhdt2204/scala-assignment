package domain.dao

import domain.models.OrderDetail
import domain.tables.OrderDetailTable
import slick.jdbc.PostgresProfile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class OrderDetailDao @Inject()(daoRunner: DaoRunner)(implicit ec: DbExecutionContext) {

  private val orderDetails = TableQuery[OrderDetailTable]

  def findAllByOrderId(orderId: Long): Future[Iterable[OrderDetail]] = daoRunner.run {
    orderDetails.filter(_.orderId === orderId).result
  }

  def saveAll(list: List[OrderDetail]): Future[Iterable[OrderDetail]] = daoRunner.run {
    (orderDetails ++= list).map(_ => list)
  }

  def deleteByOrderId(orderId: Long): Future[Int] = daoRunner.run {
    orderDetails.filter(_.orderId === orderId).delete
  }

}
