package domain.dao

import domain.models.{Order, User}
import fixtures.UnitTestDataFixture

class OrderDaoUnitTest extends UnitTestDataFixture {

  var order: Order = _
  var user: User = _

  "OrderDao#save(order)" should {

    "save a order successfully" in {
      user = userDao.save(Users.operator).futureValue
      order = orderDao.save(Orders.orderEntity.copy(userId = user.id.get)).futureValue
      Option.apply(order).isDefined mustBe true
    }

  }

  "OrderDao#findById(id: Long)" should {

    "get a order successfully" in {
      val orderOpt = orderDao.findById(order.id.get).futureValue
      orderOpt.isEmpty mustBe false

      val existOrder = orderOpt.get
      order.id.get mustEqual existOrder.id.get
      order.userId mustEqual existOrder.userId
      order.totalPrice mustEqual existOrder.totalPrice
      order.orderDate mustEqual existOrder.orderDate
    }

    "order not found" in {
      val existOrder = orderDao.findById(Int.MaxValue).futureValue
      existOrder.isEmpty mustBe true
    }

  }

  "OrderDao#findAll" should {

    "get all orders successfully" in {
      val result = orderDao.findAllByUserId(None).futureValue
      result.size mustBe 1
      result.map(_.id.get) must contain atLeastOneOf(order.id.get, 0L)
    }

  }

  "OrderDao#update(order)" should {

    "update a order successfully" in {
      val updateInput = order.copy(totalPrice = 500)
      order = orderDao.update(updateInput).futureValue

      order.id.get mustEqual updateInput.id.get
      order.totalPrice mustEqual updateInput.totalPrice
    }

  }

  "OrderDao#delete(id: Long)" should {

    "delete a order successfully" in {
      orderDao.delete(order.id.get).futureValue
      userDao.delete(user.id.get).futureValue

      val orders = orderDao.findAllByUserId(None).futureValue
      orders.size mustBe 0
    }

  }

}
