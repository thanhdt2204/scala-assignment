package domain.tables

import domain.models.OrderDetail
import slick.jdbc.PostgresProfile.api._

class OrderDetailTable(tag: Tag) extends Table[OrderDetail](tag, Some("scala"), "order_details") {

  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.Unique)

  def orderId = column[Long]("order_id")

  def productId = column[Long]("product_id")

  def price = column[Float]("price")

  def quantity = column[Int]("quantity")

  def order = foreignKey("ORDER", orderId, TableQuery[OrderTable])(_.id.get)

  def product = foreignKey("PRODUCT", productId, TableQuery[ProductTable])(_.id.get)

  def * = (id, orderId, productId, price, quantity) <> ((OrderDetail.apply _).tupled, OrderDetail.unapply)

}
