package services

import domain.dao.OrderDao
import fixtures.UnitTestDataFixture
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.when

import scala.concurrent.Future

class OrderServiceUnitTest extends UnitTestDataFixture {

  val mockOrderDao: OrderDao = mock[OrderDao]
  val orderService: OrderService = new OrderServiceImpl(mockOrderDao)

  "OrderService#findById(id: Long)" should {

    "get a order successfully" in {
      val order = Orders.orderEntity
      when(mockOrderDao.findById(anyLong())).thenReturn(Future.successful(Some(order)))

      val result = orderService.findById(1L).futureValue
      result.isEmpty mustBe false
      val actual = result.get
      actual.id.get mustEqual order.id.get
      actual.userId mustEqual order.userId
      actual.orderDate mustEqual order.orderDate
      actual.totalPrice mustEqual order.totalPrice
    }

    "order not found" in {
      when(mockOrderDao.findById(anyLong())).thenReturn(Future.successful(None))

      val result = orderService.findById(1L).futureValue
      result.isEmpty mustBe true
    }

  }

  "OrderService#findAllByUserId(userId: Option[Long])" should {

    "get all orders successfully" in {
      val orders = Orders.allOrders
      when(mockOrderDao.findAllByUserId(None)).thenReturn(Future.successful(orders))

      val result = orderService.findAllByUserId(None).futureValue
      result.size mustEqual orders.size
      result.head.id mustEqual orders.head.id
      result.head.userId mustEqual orders.head.userId
    }

    "orders no content" in {
      when(mockOrderDao.findAllByUserId(None)).thenReturn(Future.successful(Seq.empty))

      val result = orderService.findAllByUserId(None).futureValue
      result.size mustEqual 0
    }

  }

  "OrderService#save(order: Order)" should {

    "save order successfully" in {
      val order = Orders.orderEntity
      when(mockOrderDao.save(order)).thenReturn(Future.successful(order))

      val result = orderService.save(order).futureValue
      Option.apply(result).isDefined mustBe true
      result.id mustEqual order.id
      result.totalPrice mustEqual order.totalPrice
    }

  }

  "OrderService#update(order: Order)" should {

    "update order successfully" in {
      val order = Orders.orderEntity
      when(mockOrderDao.update(order)).thenReturn(Future.successful(order))

      val result = orderService.update(order).futureValue
      Option.apply(result).isDefined mustBe true
      result.id mustEqual order.id
      result.orderDate mustEqual order.orderDate
    }

  }

  "OrderService#delete(id: Long)" should {

    "delete order successfully" in {
      when(mockOrderDao.delete(anyLong())).thenReturn(Future.successful(1))

      val result = orderService.delete(1L).futureValue
      result mustEqual 1
    }

  }

}