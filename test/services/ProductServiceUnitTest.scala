package services

import domain.dao.ProductDao
import fixtures.UnitTestDataFixture
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.when

import scala.concurrent.Future

class ProductServiceUnitTest extends UnitTestDataFixture {

  val mockProductDao: ProductDao = mock[ProductDao]
  val productService: ProductService = new ProductServiceImpl(mockProductDao)

  "ProductService#findById(id: Long)" should {

    "get a product successfully" in {
      val product = Products.productEntity
      when(mockProductDao.findById(anyLong())).thenReturn(Future.successful(Some(product)))

      val result = productService.findById(1L).futureValue
      result.isEmpty mustBe false
      val actual = result.get
      actual.id.get mustEqual product.id.get
      actual.productName mustEqual product.productName
      actual.price mustEqual product.price
      actual.expDate mustEqual product.expDate
    }

    "product not found" in {
      when(mockProductDao.findById(anyLong())).thenReturn(Future.successful(None))

      val result = productService.findById(1L).futureValue
      result.isEmpty mustBe true
    }

  }

  "ProductService#findAll()" should {

    "get all products successfully" in {
      val products = Products.allProducts
      when(mockProductDao.findAll()).thenReturn(Future.successful(products))

      val result = productService.findAll().futureValue
      result.size mustEqual products.size
      result.head.id mustEqual products.head.id
      result.head.productName mustEqual products.head.productName
    }

    "products no content" in {
      when(mockProductDao.findAll()).thenReturn(Future.successful(Seq.empty))

      val result = productService.findAll().futureValue
      result.size mustEqual 0
    }

  }

  "ProductService#save(product: Product)" should {

    "save product successfully" in {
      val product = Products.productEntity
      when(mockProductDao.save(product)).thenReturn(Future.successful(product))

      val result = productService.save(product).futureValue
      Option.apply(result).isDefined mustBe true
      result.id mustEqual product.id
      result.productName mustEqual product.productName
    }

  }

  "ProductService#update(product: Product)" should {

    "update product successfully" in {
      val product = Products.productEntity
      when(mockProductDao.update(product)).thenReturn(Future.successful(product))

      val result = productService.update(product).futureValue
      Option.apply(result).isDefined mustBe true
      result.id mustEqual product.id
      result.productName mustEqual product.productName
    }

  }

  "ProductService#delete(id: Long)" should {

    "delete product successfully" in {
      when(mockProductDao.delete(anyLong())).thenReturn(Future.successful(1))

      val result = productService.delete(1L).futureValue
      result mustEqual 1
    }

  }

}