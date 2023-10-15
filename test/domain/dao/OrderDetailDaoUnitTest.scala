package domain.dao

import domain.models.{Order, OrderDetail, Product, User}
import fixtures.UnitTestDataFixture

class OrderDetailDaoUnitTest extends UnitTestDataFixture {

  var orderDetails: Iterable[OrderDetail] = _
  var user: User = _
  var product: Product = _
  var order: Order = _

  "OrderDetailDao#saveAll(list: List[OrderDetail])" should {

    "save all order detail successfully" in {
      product = productDao.save(Products.productEntity).futureValue
      user = userDao.save(Users.operator).futureValue
      order = orderDao.save(Orders.orderEntity.copy(userId = user.id.get)).futureValue
      val input = OrderDetails.orderDetailEntity.copy(orderId = order.id.get, productId = product.id.get)
      orderDetails = orderDetailDao.saveAll(List(input)).futureValue
      orderDetails.size mustEqual 1
    }

  }

  "OrderDetailDao#findAllByOrderId(orderId: Long)" should {

    "get order detail by order id successfully" in {
      orderDetails = orderDetailDao.findAllByOrderId(order.id.get).futureValue
      orderDetails.size mustEqual 1
    }

    "get order detail by order id not found" in {
      val existOrderDetails = orderDetailDao.findAllByOrderId(Int.MaxValue).futureValue
      existOrderDetails.size mustEqual 0
    }

  }

  "OrderDetailDao#deleteByOrderId(orderId: Long)" should {

    "delete a orderDetail successfully" in {
      orderDetailDao.deleteByOrderId(order.id.get).futureValue

      orderDetails = orderDetailDao.findAllByOrderId(order.id.get).futureValue
      orderDetails.size mustBe 0

      orderDao.delete(order.id.get).futureValue
      userDao.delete(user.id.get).futureValue
      productDao.delete(product.id.get).futureValue
    }

  }

}
