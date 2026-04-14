package filters

import akka.stream.Materializer
import javax.inject.Inject
import play.api.Logging
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

class RequestLoggingFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext)
    extends Filter
    with Logging {

  private val AnsiReset = "\u001b[0m"
  private val AnsiCyan = "\u001b[36m"
  private val AnsiGreen = "\u001b[32m"
  private val AnsiBlue = "\u001b[34m"
  private val AnsiYellow = "\u001b[33m"
  private val AnsiRed = "\u001b[31m"

  private def colorizeMethod(method: String): String = s"$AnsiCyan$method$AnsiReset"

  private def colorizeStatus(status: Int): String = {
    val color =
      if (status >= 500) AnsiRed
      else if (status >= 400) AnsiYellow
      else if (status >= 300) AnsiBlue
      else AnsiGreen

    s"$color$status$AnsiReset"
  }

  override def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader)
      : Future[Result] = {
    val startTime = System.currentTimeMillis()
    val shouldLogRequest = requestHeader.path.startsWith("/api")

    nextFilter(requestHeader)
      .map { result =>
        if (shouldLogRequest) {
          val durationMs = System.currentTimeMillis() - startTime
          val coloredMethod = colorizeMethod(requestHeader.method)
          val coloredStatus = colorizeStatus(result.header.status)
          logger.info(
            f"$coloredMethod%-15s ${requestHeader.uri}%-28s -> $coloredStatus (${durationMs}%d ms)"
          )
        }
        result
      }
      .recoverWith { case exception =>
        val durationMs = System.currentTimeMillis() - startTime
        logger.error(
          s"${requestHeader.method} ${requestHeader.uri} failed after ${durationMs} ms",
          exception
        )
        Future.failed(exception)
      }
  }
}
