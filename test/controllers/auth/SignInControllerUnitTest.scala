package controllers.auth

import controllers.ControllerFixture
import org.mockito.Mockito.when
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

class SignInControllerUnitTest extends ControllerFixture {

  "SignInController#login" should {

    "login successfully" in {

      // given
      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
      val request = FakeRequest(POST, "/login").withJsonBody(Json.toJson(Users.loginBody))

      // when
      val result: Future[Result] = route(app, request).get

      // then
      status(result) mustEqual OK
    }

    "login failed" in {

      // given
      when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(None))
      val request = FakeRequest(POST, "/login").withJsonBody(Json.toJson(Users.invalidLoginBody))

      // when
      val result: Future[Result] = route(app, request).get

      // then
      status(result) mustEqual BAD_REQUEST
    }

  }

}