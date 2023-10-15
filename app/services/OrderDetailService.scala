package services

import com.google.inject.ImplementedBy
import domain.dao.OrderDetailDao
import domain.models.OrderDetail

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@ImplementedBy(classOf[OrderDetailServiceImpl])
trait OrderDetailService {

  def findAllByOrderId(orderId: Long): Future[Iterable[OrderDetail]]

  def saveAll(list: List[OrderDetail]): Future[Iterable[OrderDetail]]

  def deleteByOrderId(orderId: Long): Future[Int]

}

@Singleton
class OrderDetailServiceImpl @Inject()(orderDao: OrderDetailDao) extends OrderDetailService {

  override def  findAllByOrderId(orderId: Long): Future[Iterable[OrderDetail]] = orderDao.findAllByOrderId(orderId)

  override def saveAll(list: List[OrderDetail]): Future[Iterable[OrderDetail]] = orderDao.saveAll(list)

  override def deleteByOrderId(orderId: Long): Future[Int] = orderDao.deleteByOrderId(orderId)

}
