ThisBuild / scalaVersion := "2.13.12"

val Http4sVersion = "0.23.26"
val CatsEffectVersion = "3.5.4"
val CirceVersion = "0.14.7"
val DoobieVersion = "1.0.0-RC4"

lazy val root = (project in file("."))
  .settings(
    name := "scala-http4s-app",

    libraryDependencies ++= Seq(

      // Cats Effect
      "org.typelevel" %% "cats-effect" % CatsEffectVersion,

      // http4s
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,

      // Circe JSON
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,

      // Doobie
      "org.tpolecat" %% "doobie-core" % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "org.tpolecat" %% "doobie-hikari" % DoobieVersion,

      // Logging
      "org.typelevel" %% "log4cats-slf4j" % "2.6.0",
      "ch.qos.logback" % "logback-classic" % "1.5.6"
    )
  )