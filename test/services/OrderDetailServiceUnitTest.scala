package services

import domain.dao.OrderDetailDao
import fixtures.UnitTestDataFixture
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.when

import scala.concurrent.Future

class OrderDetailServiceUnitTest extends UnitTestDataFixture {

  val mockOrderDetailDao: OrderDetailDao = mock[OrderDetailDao]
  val orderDetailService: OrderDetailService = new OrderDetailServiceImpl(mockOrderDetailDao)

  "OrderDetailService#findAllByOrderId(orderId: Long)" should {

    "get all order detail successfully" in {
      val allOrderDetails = OrderDetails.allOrderDetails
      when(mockOrderDetailDao.findAllByOrderId(anyLong())).thenReturn(Future.successful(allOrderDetails))

      val result = orderDetailService.findAllByOrderId(1L).futureValue
      result.size mustEqual allOrderDetails.size
    }

    "order detail not found" in {
      when(mockOrderDetailDao.findAllByOrderId(anyLong())).thenReturn(Future.successful(Seq.empty))

      val result = orderDetailService.findAllByOrderId(1L).futureValue
      result.size mustEqual 0
    }

  }

  "OrderDetailService#saveAll(list: List[OrderDetail])" should {

    "save order detail successfully" in {
      val allOrderDetails = OrderDetails.allOrderDetails
      when(mockOrderDetailDao.saveAll(allOrderDetails.toList)).thenReturn(Future.successful(allOrderDetails))

      val result = orderDetailService.saveAll(allOrderDetails.toList).futureValue
      result.size mustEqual allOrderDetails.size
    }

  }

  "OrderDetailService#deleteByOrderId(orderId: Long)" should {

    "delete order detail successfully" in {
      when(mockOrderDetailDao.deleteByOrderId(anyLong())).thenReturn(Future.successful(1))

      val result = orderDetailService.deleteByOrderId(1L).futureValue
      result mustEqual 1
    }

  }

}