package com.dharanish.streamforge.routes

import cats.effect.IO
import org.http4s.dsl.io._
import org.http4s.HttpRoutes
import io.circe.generic.auto._
import org.http4s.circe._

object HealthRoutes {

  case class Info(name: String, version: String, description: String)

  // Provide an implicit EntityEncoder for Info using Circe
  import io.circe.Encoder
  import org.http4s.circe.CirceEntityEncoder._

  implicit val infoEncoder: Encoder[Info] = io.circe.generic.semiauto.deriveEncoder[Info]

  val routes = HttpRoutes.of[IO] {
    case GET -> Root / "health" =>
      Ok("Server is running 🚀")
    case GET -> Root =>
      Ok("Welcome to StreamForge!")
    case GET -> Root / "info" =>
      Ok(Info("StreamForge", "1.0.0", "A simple Scala http4s server project"))
  }
}