package user

import fixtures.DataFixture
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws._
import play.api.test.Helpers._
import play.api.test._

class UserIT extends DataFixture {

  "SUCCESS" should {

    var accessToken: Option[String] = None
    var userResponse: WSResponse = null
    var user: UserOutput = null

    "POST login successfully" in new WithServer {
      val loginRes: WSResponse = await(WsTestClient.wsUrl("/login").post(Json.toJson(Users.loginBody)))
      accessToken = loginRes.header(authHeaderKey)
      accessToken.isDefined mustBe true
    }

    "POST user successfully" in new WithServer {
      // when
      userResponse = await(
        WsTestClient.wsUrl("/users").addHttpHeaders(authHeaderKey -> accessToken.get)
          .post(Json.toJson(Users.userInput))
      )
      // then
      userResponse.status mustEqual 201
      user = userResponse.body[JsValue].as[UserOutput]
    }

    "GET user by id successfully" in new WithServer {
      // when
      userResponse = await(
        WsTestClient.wsUrl("/users/" + user.id).addHttpHeaders(authHeaderKey -> accessToken.get).get())
      // then
      userResponse.status mustEqual 200
      val actualUser: UserOutput = userResponse.body[JsValue].as[UserOutput]
      verifyUser(actualUser, user)
    }

    "DELETE user by id successfully" in new WithServer {
      // when
      userResponse = await(
        WsTestClient.wsUrl("/users/" + user.id).addHttpHeaders(authHeaderKey -> accessToken.get).delete())
      // then
      userResponse.status mustEqual 200
    }

    "GET user by id not found after deleted" in new WithServer {
      // when
      userResponse = await(
        WsTestClient.wsUrl("/users/" + user.id).addHttpHeaders(authHeaderKey -> accessToken.get).get())
      // then
      userResponse.status mustEqual 404
    }

  }

  private def verifyUser(actual: UserOutput, expected: UserOutput): Unit = {
    actual.id mustEqual expected.id
    actual.firstName mustEqual expected.firstName
    actual.lastName mustEqual expected.lastName
    actual.email mustEqual expected.email
  }

}
