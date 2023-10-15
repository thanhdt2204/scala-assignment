package domain.dao

import domain.models.Product
import fixtures.UnitTestDataFixture

import java.util.UUID

class ProductDaoUnitTest extends UnitTestDataFixture {

  var product: Product = _

  "ProductDao#save(product)" should {

    "save a product successfully" in {
      product = productDao.save(Products.productEntity).futureValue
      Option.apply(product).isDefined mustBe true
    }

  }

  "ProductDao#findById(id: Long)" should {

    "get a product successfully" in {
      val productOpt = productDao.findById(product.id.get).futureValue
      productOpt.isEmpty mustBe false

      val existProduct = productOpt.get
      product.id.get mustEqual existProduct.id.get
      product.productName mustEqual existProduct.productName
      product.price mustEqual existProduct.price
      product.expDate mustEqual existProduct.expDate
    }

    "product not found" in {
      val existProduct = productDao.findById(Int.MaxValue).futureValue
      existProduct.isEmpty mustBe true
    }

  }

  "ProductDao#findAll" should {

    "get all products successfully" in {
      val result = productDao.findAll().futureValue
      result.size mustBe 1
      result.map(_.id.get) must contain atLeastOneOf(product.id.get, 0L)
    }

  }

  "ProductDao#update(product)" should {

    "update a product successfully" in {
      val updateInput = product.copy(productName = UUID.randomUUID().toString)
      product = productDao.update(updateInput).futureValue

      product.id.get mustEqual updateInput.id.get
      product.productName mustEqual updateInput.productName
    }

  }

  "ProductDao#delete(id: Long)" should {

    "delete a product successfully" in {
      productDao.delete(product.id.get).futureValue

      val products = productDao.findAll().futureValue
      products.size mustBe 0
    }

  }

}
