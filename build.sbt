import sbt.Keys._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

name := "swagger-check"

lazy val root = project.in(file(".")).settings(publishArtifact := false)
  .aggregate(jsonSchemaModel, playJsonSchema)

lazy val playJsonSchema = project.in(file("play-json-schema"))

lazy val jsonSchemaModel = project.in(file("json-schema-model"))

Common.settings

val playVersion = "2.4.3"

scalacOptions := Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "io.swagger" % "swagger-parser" % "1.0.10",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scalacheck" %% "scalacheck" % "1.12.5",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.5.4",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.5.4",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.5.4",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.3",
  "com.typesafe.play" %% "play-test" % playVersion % "provided",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.mockito" % "mockito-core" % "1.10.19" % "test"
)
