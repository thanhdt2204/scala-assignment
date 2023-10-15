package domain.dao

import javax.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import exceptions.BadRequestException

import scala.concurrent.Future
import scala.reflect.ClassTag

@Singleton
class PasswordInfoDao @Inject()(userDao: UserDao)(implicit val classTag: ClassTag[PasswordInfo], ec: DbExecutionContext)
  extends DelegableAuthInfoDAO[PasswordInfo] {

  /**
   * Finds passwordInfo for specified loginInfo
   */
  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    userDao.find(loginInfo).map(_.map(_.passwordInfo))

  /**
   * Adds new passwordInfo for specified loginInfo
   */
  override def add(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] =
    update(loginInfo, passwordInfo)

  /**
   * Updates passwordInfo for specified loginInfo
   */
  override def update(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] =
    userDao.find(loginInfo).flatMap {
      case Some(user) => userDao.update(user.copy(password = passwordInfo.password)).map(_.passwordInfo)
      case None => throw new BadRequestException("User with email " + loginInfo.providerKey + " not found")
    }

  /**
   * Adds new passwordInfo for specified loginInfo
   */
  override def save(loginInfo: LoginInfo, passwordInfo: PasswordInfo): Future[PasswordInfo] =
    update(loginInfo, passwordInfo)

  /**
   * Removes passwordInfo for specified loginInfo
   */
  override def remove(loginInfo: LoginInfo): Future[Unit] =
    update(loginInfo, PasswordInfo("", "")).map(_ => ())
}
