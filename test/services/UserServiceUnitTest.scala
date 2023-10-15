package services

import domain.dao.UserDao
import fixtures.UnitTestDataFixture
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.when

import scala.concurrent.Future

class UserServiceUnitTest extends UnitTestDataFixture {

  val mockUserDao: UserDao = mock[UserDao]
  val userService: UserService = new UserServiceImpl(mockUserDao)

  "UserService#findById(id: Long)" should {

    "get a user successfully" in {
      val user = Users.operator
      when(mockUserDao.findById(anyLong())).thenReturn(Future.successful(Some(user)))

      val result = userService.findById(1L).futureValue
      result.isEmpty mustBe false
      val actual = result.get
      actual.id.get mustEqual user.id.get
      actual.email mustEqual user.email
      actual.firstName mustEqual user.firstName
      actual.lastName mustEqual user.lastName
    }

    "user not found" in {
      when(mockUserDao.findById(anyLong())).thenReturn(Future.successful(None))

      val result = userService.findById(1L).futureValue
      result.isEmpty mustBe true
    }

  }

  "UserService#findAll()" should {

    "get all users successfully" in {
      val users = Users.allUsers
      when(mockUserDao.findAll()).thenReturn(Future.successful(users))

      val result = userService.findAll().futureValue
      result.size mustEqual users.size
      result.head.id mustEqual users.head.id
      result.head.email mustEqual users.head.email
    }

    "users no content" in {
      when(mockUserDao.findAll()).thenReturn(Future.successful(Seq.empty))

      val result = userService.findAll().futureValue
      result.size mustEqual 0
    }

  }

  "UserService#save(user: User)" should {

    "save user successfully" in {
      val user = Users.operator
      when(mockUserDao.save(user)).thenReturn(Future.successful(user))

      val result = userService.save(user).futureValue
      Option.apply(result).isDefined mustBe true
      result.id mustEqual user.id
      result.email mustEqual user.email
    }

  }

  "UserService#update(user: User)" should {

    "update user successfully" in {
      val user = Users.operator
      when(mockUserDao.update(user)).thenReturn(Future.successful(user))

      val result = userService.update(user).futureValue
      Option.apply(result).isDefined mustBe true
      result.id mustEqual user.id
      result.firstName mustEqual user.firstName
    }

  }

  "UserService#delete(id: Long)" should {

    "delete user successfully" in {
      when(mockUserDao.delete(anyLong())).thenReturn(Future.successful(1))

      val result = userService.delete(1L).futureValue
      result mustEqual 1
    }

  }

}