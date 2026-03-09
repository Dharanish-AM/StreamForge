package errors

import play.api.http.Status

abstract class ApiException(
    val statusCode: Int,
    val errorCode: String,
    override val getMessage: String
) extends RuntimeException(getMessage)

final case class BadRequestException(message: String)
    extends ApiException(Status.BAD_REQUEST, "bad_request", message)

final case class NotFoundException(message: String)
    extends ApiException(Status.NOT_FOUND, "not_found", message)
