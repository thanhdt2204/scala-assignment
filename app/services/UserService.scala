package services

import com.google.inject.ImplementedBy
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import domain.dao.UserDao
import domain.models.User

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@ImplementedBy(classOf[UserServiceImpl])
trait UserService extends IdentityService[User] {

  def findAll(): Future[Iterable[User]]

  def findById(id: Long): Future[Option[User]]

  def save(user: User): Future[User]

  def update(user: User): Future[User]

  def delete(id: Long): Future[Int]

}

@Singleton
class UserServiceImpl @Inject()(userDao: UserDao) extends UserService {

  override def findAll(): Future[Iterable[User]] = userDao.findAll()

  override def findById(id: Long): Future[Option[User]] = userDao.findById(id)

  override def save(user: User): Future[User] = userDao.save(user)

  override def update(user: User): Future[User] = userDao.update(user)

  override def delete(id: Long): Future[Int] = userDao.delete(id)

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDao.find(loginInfo)

}
