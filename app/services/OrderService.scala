package services

import com.google.inject.ImplementedBy
import domain.dao.OrderDao
import domain.models.Order

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {

  def findAllByUserId(userId: Option[Long]): Future[Iterable[Order]]

  def findById(id: Long): Future[Option[Order]]

  def save(order: Order): Future[Order]

  def update(order: Order): Future[Order]

  def delete(id: Long): Future[Int]

}

@Singleton
class OrderServiceImpl @Inject()(orderDao: OrderDao) extends OrderService {

  override def findAllByUserId(userId: Option[Long]): Future[Iterable[Order]] = orderDao.findAllByUserId(userId)

  override def findById(id: Long): Future[Option[Order]] = orderDao.findById(id)

  override def save(order: Order): Future[Order] = orderDao.save(order)

  override def update(order: Order): Future[Order] = orderDao.update(order)

  override def delete(id: Long): Future[Int] = orderDao.delete(id)

}
