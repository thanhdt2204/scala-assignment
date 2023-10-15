package domain.dao

import domain.models.User
import fixtures.UnitTestDataFixture

import java.util.UUID

class UserDaoUnitTest extends UnitTestDataFixture {

  var user: User = _

  "UserDao#save(user)" should {

    "save a user successfully" in {
      user = userDao.save(Users.operator).futureValue
      Option.apply(user).isDefined mustBe true
    }

  }

  "UserDao#findById(id: Long)" should {

    "get a user successfully" in {
      val userOpt = userDao.findById(user.id.get).futureValue
      userOpt.isEmpty mustBe false

      val existUser = userOpt.get
      user.id.get mustEqual existUser.id.get
      user.email mustEqual existUser.email
      user.firstName mustEqual existUser.firstName
      user.lastName mustEqual existUser.lastName
    }

    "user not found" in {
      val existUser = userDao.findById(Int.MaxValue).futureValue
      existUser.isEmpty mustBe true
    }

  }

  "UserDao#findAll" should {

    "get all users successfully" in {
      val result = userDao.findAll().futureValue
      result.size mustBe 2
      result.map(_.id.get) must contain atLeastOneOf(user.id.get, 1L)
    }

  }

  "UserDao#update(user)" should {

    "update a user successfully" in {
      val updateInput = user.copy(email = UUID.randomUUID().toString + "@gmail.com")
      user = userDao.update(updateInput).futureValue

      user.id.get mustEqual updateInput.id.get
      user.email mustEqual updateInput.email
    }

  }

  "UserDao#delete(id: Long)" should {

    "delete a user successfully" in {
      userDao.delete(user.id.get).futureValue

      val users = userDao.findAll().futureValue
      users.size mustBe 1
    }

  }

}
