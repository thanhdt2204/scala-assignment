package domain.dao

import com.mohiva.play.silhouette.api.LoginInfo
import domain.models.User
import domain.tables.UserTable
import exceptions.{BadRequestException, NotFoundException}
import slick.jdbc.PostgresProfile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@Singleton
class UserDao @Inject()(daoRunner: DaoRunner)(implicit ec: DbExecutionContext) {

  private val users = TableQuery[UserTable]

  def findAll(): Future[Iterable[User]] = daoRunner.run {
    users.result
  }

  def findById(id: Long): Future[Option[User]] = daoRunner.run {
    users.filter(_.id === id).result.headOption
  }

  def save(user: User): Future[User] = daoRunner.run {
    users.filter(_.email === user.email).take(1).result.headOption.flatMap {
      case Some(_) =>
        throw new BadRequestException("Email " + user.email + " already exists")
      case None =>
        users returning users += user
    }
  }

  private def checkExistEmailInOtherAccount(id: Long, email: String): Boolean = Await.result(
    daoRunner.run {
      users.filter(user => user.email === email && user.id =!= id).exists.result
    }, Duration.Inf
  )

  private def checkExistUser(id: Long): Boolean = Await.result(
    daoRunner.run {
      users.filter(_.id === id).exists.result
    }, Duration.Inf
  )

  def update(user: User): Future[User] = daoRunner.run {
    // Check user exist
    if (!checkExistUser(user.id.get))
      throw new NotFoundException("User not found with id " + user.id.get)

    // Check duplicate email
    if (checkExistEmailInOtherAccount(user.id.get, user.email)) {
      throw new BadRequestException("Email " + user.email + " already exists in other account")
    } else {
      users.filter(_.id === user.id).update(user).map(_ => user)
    }
  }

  def delete(id: Long): Future[Int] = daoRunner.run {
    users.filter(_.id === id).delete
  }

  def find(loginInfo: LoginInfo): Future[Option[User]] = daoRunner.run {
    users.filter(_.email === loginInfo.providerKey).result.headOption
  }

  def findByEmail(email: String): Future[Option[User]] = daoRunner.run {
    users.filter(_.email === email).result.headOption
  }

}
