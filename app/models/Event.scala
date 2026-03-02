package models

import java.sql.Timestamp

case class Event(
    id: Int,
    userId: Int,
    amount: Double,
    eventType: String,
    createdAt: Timestamp
)
