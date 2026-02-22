ThisBuild / scalaVersion := "2.13.14"

val http4sVersion = "0.23.23"
val circeVersion  = "0.14.6"
val catsVersion   = "3.5.2"

lazy val root = (project in file("."))
  .settings(
    name := "streamforge",

    libraryDependencies ++= Seq(

      // http4s
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,

      // circe JSON
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      // cats effect
      "org.typelevel" %% "cats-effect" % catsVersion,

      // logging
      "ch.qos.logback" % "logback-classic" % "1.4.11"
    )
  )