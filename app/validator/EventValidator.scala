package validator

import models.Event

object EventValidator {
  def validateEvent(event: Event): Either[String, Event] =
    if (event.userId <= 0) {
      Left("User ID must be a positive integer.")
    } else if (event.amount < 0) {
      Left("Amount cannot be negative.")
    } else if (event.eventType.trim.isEmpty) {
      Left("Event type cannot be empty.")
    } else {
      Right(event)
    }
}
