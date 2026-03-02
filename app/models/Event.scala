package models

import java.time.Instant

case class Event(
    id: Int,
    userId: Int,
    amount: BigDecimal,
    eventType: String,
    createdAt: Instant
)
