package domain.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

case class OrderResponse(id: Long,
                         customer: String,
                         orderDate: LocalDateTime,
                         totalPrice: Float,
                         orderDetails: Iterable[OrderDetail]) {}

object OrderResponse {
  implicit val format: OFormat[OrderResponse] = Json.format[OrderResponse]
}

