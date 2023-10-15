package controllers

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.test._
import domain.models._
import fixtures.{TestApplication, UnitTestDataFixture}
import play.api.Application
import services._
import utils.auth.JWTEnvironment

import scala.concurrent.ExecutionContext.Implicits.global

class ControllerFixture extends UnitTestDataFixture {

  val mockUserService: UserService = mock[UserService]
  val identity: User = Users.admin
  implicit val env: Environment[JWTEnvironment] = new FakeEnvironment[JWTEnvironment](Seq(identity.loginInfo -> identity))
  implicit override lazy val app: Application = TestApplication.app()

}
