package dto

import play.api.libs.json.{Json, OFormat}

case class ErrorResponse(
    error: String,
    message: String
)

object ErrorResponse {
  implicit val format: OFormat[ErrorResponse] = Json.format[ErrorResponse]
}
