package dto

import play.api.libs.json.{Json, OWrites}

case class KpiResponse(
    totalEvents: Int,
    totalVolume: BigDecimal,
    averageAmount: BigDecimal,
    uniqueUsers: Int
)

object KpiResponse {
  implicit val writes: OWrites[KpiResponse] = Json.writes[KpiResponse]
}
