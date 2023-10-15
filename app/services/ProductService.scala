package services

import com.google.inject.ImplementedBy
import domain.dao.ProductDao
import domain.models.Product

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@ImplementedBy(classOf[ProductServiceImpl])
trait ProductService {

  def findAll(): Future[Iterable[Product]]

  def findById(id: Long): Future[Option[Product]]

  def save(product: Product): Future[Product]

  def update(product: Product): Future[Product]

  def delete(id: Long): Future[Int]

  def saveAll(list: Iterable[Product]): Future[Iterable[Product]]

}

@Singleton
class ProductServiceImpl @Inject()(productDao: ProductDao) extends ProductService {

  override def findAll(): Future[Iterable[Product]] = productDao.findAll()

  override def findById(id: Long): Future[Option[Product]] = productDao.findById(id)

  override def save(product: Product): Future[Product] = productDao.save(product)

  override def update(product: Product): Future[Product] = productDao.update(product)

  override def delete(id: Long): Future[Int] = productDao.delete(id)

  override def saveAll(list: Iterable[Product]): Future[Iterable[Product]] = productDao.saveAll(list)

}
