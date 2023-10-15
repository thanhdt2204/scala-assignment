package domain.dao

import domain.models.Product
import domain.tables.ProductTable
import exceptions.NotFoundException
import slick.jdbc.PostgresProfile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class ProductDao @Inject()(daoRunner: DaoRunner)(implicit ec: DbExecutionContext) {

  private val products = TableQuery[ProductTable]

  def findAll(): Future[Iterable[Product]] = daoRunner.run {
    products.result
  }

  def findById(id: Long): Future[Option[Product]] = daoRunner.run {
    products.filter(_.id === id).result.headOption
  }

  def save(product: Product): Future[Product] = daoRunner.run {
    products returning products += product
  }

  def update(product: Product): Future[Product] = daoRunner.run {
    products.filter(_.id === product.id).take(1).result.headOption.flatMap {
      case Some(_) =>
        products.filter(_.id === product.id).update(product).map(_ => product)
      case None =>
          throw new NotFoundException("Product not found with id " + product.id.get)
    }
  }

  def delete(id: Long): Future[Int] = daoRunner.run {
    products.filter(_.id === id).delete
  }

  def saveAll(list: Iterable[Product]): Future[Iterable[Product]] = daoRunner.run {
    (products ++= list).map(_ => list)
  }

}
