package dto

import java.time.Instant
import play.api.libs.json.{Json, OWrites}

case class EventResponse(
    id: Int,
    userId: Int,
    amount: BigDecimal,
    eventType: String,
    createdAt: Instant
)

object EventResponse {
  implicit val writes: OWrites[EventResponse] = Json.writes[EventResponse]
}
