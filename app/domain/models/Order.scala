package domain.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

case class Order(id: Option[Long],
                 userId: Long,
                 orderDate: LocalDateTime,
                 totalPrice: Float) {}

object Order {
  implicit val format: OFormat[Order] = Json.format[Order]
}

