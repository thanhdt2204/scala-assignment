package product

import domain.models.Product
import fixtures.DataFixture
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws._
import play.api.test.Helpers._
import play.api.test._

class ProductIT extends DataFixture {

  "SUCCESS" should {

    var accessToken: Option[String] = None
    var productResponse: WSResponse = null
    var product: Product = null

    "POST login successfully" in new WithServer {
      val loginRes: WSResponse = await(WsTestClient.wsUrl("/login").post(Json.toJson(Users.loginBody)))
      accessToken = loginRes.header(authHeaderKey)
      accessToken.isDefined mustBe true
    }

    "POST product successfully" in new WithServer {
      // when
      productResponse = await(
        WsTestClient.wsUrl("/products").addHttpHeaders(authHeaderKey -> accessToken.get)
          .post(Json.toJson(Products.productInput))
      )
      // then
      productResponse.status mustEqual 201
      product = productResponse.body[JsValue].as[Product]
    }

    "GET product by id successfully" in new WithServer {
      // when
      productResponse = await(
        WsTestClient.wsUrl("/products/" + product.id.get).addHttpHeaders(authHeaderKey -> accessToken.get).get())
      // then
      productResponse.status mustEqual 200
      val actualProduct: Product = productResponse.body[JsValue].as[Product]
      verifyProduct(actualProduct, product)
    }

    "DELETE product by id successfully" in new WithServer {
      // when
      productResponse = await(
        WsTestClient.wsUrl("/products/" + product.id.get).addHttpHeaders(authHeaderKey -> accessToken.get).delete())
      // then
      productResponse.status mustEqual 200
    }

    "GET product by id not found after deleted" in new WithServer {
      // when
      productResponse = await(
        WsTestClient.wsUrl("/products/" + product.id.get).addHttpHeaders(authHeaderKey -> accessToken.get).get())
      // then
      productResponse.status mustEqual 404
    }

  }

  "FAILED" should {

    "POST login fail when invalid body" in new WithServer {
      // when
      val loginRes: WSResponse = await(WsTestClient.wsUrl("/login").post(Json.toJson(Users.loginInvalidBody)))

      // then
      loginRes.status mustEqual 400
    }

  }

  private def verifyProduct(actual: Product, expected: Product): Unit = {
    actual.id.get mustEqual expected.id.get
    actual.productName mustEqual expected.productName
    actual.price mustEqual expected.price
    actual.expDate mustEqual expected.expDate
  }

}
