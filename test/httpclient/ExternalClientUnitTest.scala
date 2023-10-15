package httpclient

import domain.models.Product
import fixtures.UnitTestDataFixture
import org.mockito.Mockito.{never, times, verify, when}
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatest.BeforeAndAfterEach
import play.api.http.HttpVerbs
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{BodyWritable, WSAuthScheme, WSClient, WSRequest, WSRequestFilter, WSResponse}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

class ExternalClientUnitTest extends UnitTestDataFixture with BeforeAndAfterEach {

  val mockWSClient: WSClient = mock[WSClient]
  val wsRequest: WSRequest = mock[WSRequest]
  val wsResponse: WSResponse = mock[WSResponse]

  val httpClient: ExternalClient = new ExternalClient(mockWSClient)(global)

  val baseUrl = "https://thanh-doan-1.free.beeceptor.com"
  val apiPath = "/external/products"
  val products: Seq[Product] = Products.allProducts

  val productJsValue: JsValue = Json.toJson(products)

  override def beforeEach(): Unit = {
    when(mockWSClient.url(ArgumentMatchers.any[String])).thenReturn(wsRequest)
    when(wsRequest.withMethod(ArgumentMatchers.any[String])).thenReturn(wsRequest)
    when(wsRequest.withQueryStringParameters(ArgumentMatchers.any())).thenReturn(wsRequest)
    when(wsRequest.withBody(ArgumentMatchers.any[JsValue])(ArgumentMatchers.any[BodyWritable[JsValue]])).thenReturn(wsRequest)
    when(wsRequest.addHttpHeaders(ArgumentMatchers.any())).thenReturn(wsRequest)
    when(wsRequest.withAuth(ArgumentMatchers.any[String], ArgumentMatchers.any[String], ArgumentMatchers.any[WSAuthScheme])).thenReturn(wsRequest)
    when(wsRequest.withRequestFilter(ArgumentMatchers.any[WSRequestFilter])).thenReturn(wsRequest)
  }

  "ExternalProductClient#get" should {

    "get all external product successfully" in {

      // when
      when(wsRequest.execute()).thenReturn(Future.successful(wsResponse))
      when(wsResponse.status).thenReturn(200)
      when(wsResponse.body[JsValue]).thenReturn(productJsValue)
      val result = httpClient.get[Seq[Product]](s"$apiPath").futureValue

      // then
      result.size mustEqual products.size
      verify(mockWSClient, Mockito.only()).url(ArgumentMatchers.eq(s"$baseUrl$apiPath"))
      verify(wsRequest, times(1)).withMethod(ArgumentMatchers.eq(HttpVerbs.GET))
      verify(wsRequest, never()).withQueryStringParameters(ArgumentMatchers.any())
      verify(wsRequest, never()).withBody(ArgumentMatchers.any[JsValue])(ArgumentMatchers.any[BodyWritable[JsValue]])
      verify(wsRequest, never()).addHttpHeaders(ArgumentMatchers.any())
      verify(wsRequest, times(1))
        .withAuth(ArgumentMatchers.eq(null), ArgumentMatchers.eq(null), ArgumentMatchers.eq(WSAuthScheme.BASIC))
      verify(wsRequest, times(1)).withRequestFilter(ArgumentMatchers.any[WSRequestFilter])
    }

  }

}
