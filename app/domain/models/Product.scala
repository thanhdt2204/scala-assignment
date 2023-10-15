package domain.models

import play.api.libs.json.{Json, OFormat}
import java.time.LocalDateTime

case class Product(id: Option[Long],
                   productName: String,
                   price: Float,
                   expDate: LocalDateTime)

object Product {
  implicit val format: OFormat[Product] = Json.format[Product]
}

