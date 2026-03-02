package models

import java.time.Instant
import play.api.libs.json.OFormat
import play.api.libs.json.Json

case class Event(
    id: Int,
    userId: Int,
    amount: BigDecimal,
    eventType: String,
    createdAt: Instant
)

object Event {
  implicit val format: OFormat[Event] = Json.format[Event]
}
