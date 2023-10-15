package domain.models

import play.api.libs.json.{Json, OFormat}

case class OrderDetail(id: Option[Long],
                       orderId: Long,
                       productId: Long,
                       price: Float,
                       quantity: Int) {}

object OrderDetail {
  implicit val format: OFormat[OrderDetail] = Json.format[OrderDetail]
}

