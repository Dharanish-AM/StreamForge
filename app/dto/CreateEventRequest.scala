package dto

import java.time.Instant
import play.api.libs.json.{Json, OFormat}

case class CreateEventRequest(
    userId: Int,
    amount: BigDecimal,
    eventType: String,
)

object CreateEventRequest {
  implicit val format: OFormat[CreateEventRequest] = Json.format[CreateEventRequest]
}
