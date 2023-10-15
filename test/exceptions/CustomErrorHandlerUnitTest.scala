package exceptions

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.NotFound
import fixtures.UnitTestDataFixture
import play.api.test.Helpers._
import play.api.test._

class CustomErrorHandlerUnitTest extends UnitTestDataFixture {
  "CustomErrorHandler" should {

    "return bad request when a page has not been found" in {
      // when
      val responseFuture = new CustomErrorHandler().onClientError(FakeRequest(GET, "/fake"), NotFound.intValue, "")

      // then
      status(responseFuture) mustBe StatusCodes.BadRequest.intValue
    }

    "return bad request when server return BadRequestException" in {
      // when
      val responseFuture = new CustomErrorHandler().onServerError(FakeRequest(GET, "/fake"), new BadRequestException(""))

      // then
      status(responseFuture) mustBe StatusCodes.BadRequest.intValue
    }

    "return not found when server return NotFoundException" in {
      // when
      val responseFuture = new CustomErrorHandler().onServerError(FakeRequest(GET, "/fake"), new NotFoundException(""))

      // then
      status(responseFuture) mustBe StatusCodes.NotFound.intValue
    }

    "return internal server error when server return Exception" in {
      // when
      val responseFuture = new CustomErrorHandler().onServerError(FakeRequest(GET, "/fake"), new Exception(""))

      // then
      status(responseFuture) mustBe StatusCodes.InternalServerError.intValue
    }


  }

}
