package services

import com.google.inject.ImplementedBy
import domain.models.Product
import httpclient.ExternalClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@ImplementedBy(classOf[ExternalProductServiceImpl])
trait ExternalProductService {

  def findAll(): Future[Iterable[Product]]

}

@Singleton
class ExternalProductServiceImpl @Inject()(client: ExternalClient) extends ExternalProductService {

  private val URI_PRODUCT = "/products"

  override def findAll(): Future[Iterable[Product]] = client.get[Seq[Product]](URI_PRODUCT)

}