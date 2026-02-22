import org.http4s.server.Router
import org.http4s.implicits._
import com.dharanish.streamforge.routes.HealthRoutes

import cats.effect.{IOApp, IO, ExitCode}
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s.{Host, Port}

object Main extends IOApp {
  val httpApp =
    Router(
      "/" -> HealthRoutes.routes
    ).orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString("0.0.0.0").get)
      .withPort(Port.fromInt(8080).get)
      .withHttpApp(httpApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}