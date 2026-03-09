package dto

import play.api.libs.json.{Json, OFormat}

case class UpdateEventRequest(
    userId: Int,
    amount: BigDecimal,
    eventType: String
)

object UpdateEventRequest {
  implicit val format: OFormat[UpdateEventRequest] = Json.format[UpdateEventRequest]
}
