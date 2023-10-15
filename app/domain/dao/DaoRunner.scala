package domain.dao

import com.google.inject.ImplementedBy
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import javax.inject.{Inject, Singleton}

import scala.concurrent.Future

@ImplementedBy(classOf[DaoRunnerImpl])
trait DaoRunner {

  import slick.dbio._

  def run[R](a: DBIO[R]): Future[R]

}

@Singleton
class DaoRunnerImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends DaoRunner with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  override def run[R](a: DBIO[R]): Future[R] = db.run(a.transactionally)

}
