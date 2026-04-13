name := """StreamForge"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.18"

val jacksonVersion = "2.15.2"

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.play" %% "play-slick" % "5.1.0",
  "org.flywaydb" % "flyway-core" % "11.0.1",
  "org.flywaydb" % "flyway-database-postgresql" % "11.0.1",
  "org.postgresql" % "postgresql" % "42.7.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test
)

dependencyOverrides ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % jacksonVersion,
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % jacksonVersion,
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % jacksonVersion,
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % jacksonVersion
)

javaOptions ++= Seq(
  "--add-opens=java.base/java.lang=ALL-UNNAMED",
  "--add-opens=java.base/java.util=ALL-UNNAMED",
  "--add-opens=java.base/sun.security.ssl=ALL-UNNAMED"
)

run / fork := true
Test / fork := true
