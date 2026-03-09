package errors

import dto.ErrorResponse
import javax.inject.Singleton
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.RequestHeader
import play.api.mvc.Results.Status
import play.api.http.HttpErrorHandler

import scala.concurrent.Future

@Singleton
class JsonHttpErrorHandler extends HttpErrorHandler {

  private val logger = Logger(this.getClass)

  override def onClientError(
      request: RequestHeader,
      statusCode: Int,
      message: String
  ): Future[play.api.mvc.Result] = {
    val response = statusCode match {
      case 400 => ErrorResponse("bad_request", if (message.nonEmpty) message else "Bad request")
      case 401 => ErrorResponse("unauthorized", if (message.nonEmpty) message else "Unauthorized")
      case 403 => ErrorResponse("forbidden", if (message.nonEmpty) message else "Forbidden")
      case 404 => ErrorResponse("not_found", "The requested resource was not found")
      case _   => ErrorResponse("client_error", if (message.nonEmpty) message else "Client error")
    }

    Future.successful(Status(statusCode)(Json.toJson(response)))
  }

  override def onServerError(
      request: RequestHeader,
      exception: Throwable
  ): Future[play.api.mvc.Result] = exception match {
    case apiException: ApiException =>
      Future.successful(
        Status(apiException.statusCode)(
          Json.toJson(ErrorResponse(apiException.errorCode, apiException.getMessage))
        )
      )

    case other =>
      logger.error(s"Unhandled server error on ${request.method} ${request.uri}", other)
      Future.successful(
        Status(500)(
          Json.toJson(
            ErrorResponse("internal_server_error", "An unexpected error occurred")
          )
        )
      )
  }
}
