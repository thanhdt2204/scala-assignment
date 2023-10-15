package domain.tables

import domain.models.Product
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime

class ProductTable (tag: Tag) extends Table[Product](tag, Some("scala"), "products") {

  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.Unique)

  def productName = column[String]("product_name")

  def price = column[Float]("price")

  def expDate = column[LocalDateTime]("exp_date")

  /**
   * This is the table's default "projection".
   * It defines how the columns are converted to and from the Product object.
   */
  def * = (id, productName, price, expDate) <> ((Product.apply _).tupled, Product.unapply)

}
