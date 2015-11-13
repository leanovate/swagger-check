import sbt.Keys._

name := "swagger-check"

lazy val root = project.in(file(".")).settings(publishArtifact := false)
  .aggregate(jsonSchemaModel, jsonSchemaGen, playJsonSchema)

lazy val playJsonSchema = project.in(file("play-json-schema"))

lazy val jsonSchemaModel = project.in(file("json-schema-model"))

lazy val jsonSchemaGen = project.in(file("json-schema-gen")).dependsOn(jsonSchemaModel)

Common.settings

val playVersion = "2.4.3"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scalacheck" %% "scalacheck" % "1.12.5",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.5.4",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.5.4",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.5.4",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.3",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.4.5",
  "com.typesafe.play" %% "play-test" % playVersion % "provided",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value
)
