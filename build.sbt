import sbt._
import sbt.Keys._
import sbt.CrossVersion

name := "swagger-check"

lazy val root = project.in(file(".")).settings(publishArtifact := false)
  .aggregate(jsonSchemaModel, jsonSchemaGen, jsonSchemaPlay, jsonSchemaJackson, swaggerCheckCore)

lazy val jsonSchemaModel = project.in(file("json-schema-model"))

lazy val jsonSchemaGen = project.in(file("json-schema-gen")).dependsOn(jsonSchemaModel)

lazy val jsonSchemaPlay = project.in(file("json-schema-play"))
  .settings(Common.settings: _*)
  .settings(
    name := "json-schema-play",
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 12)) => Seq("com.typesafe.play" %% "play-json" % Common.playVersion26)
        case Some((2, _)) => Seq("com.typesafe.play" %% "play-json" % Common.playVersion)
        case _ => fail("Invalid scala version")
      }
    }
  )
  .dependsOn(jsonSchemaModel, jsonSchemaGen % Test)

lazy val jsonSchemaJackson = project.in(file("json-schema-jackson")).dependsOn(jsonSchemaModel)

lazy val swaggerCheckCore = project.in(file("swagger-check-core"))
  .settings(Common.settings: _*)
  .settings(
    name := "swagger-check-core",
    libraryDependencies ++=
      (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 12)) => Seq("com.typesafe.play" %% "play-test" % Common.playVersion26 % "provided")
        case Some((2, _)) => Seq("com.typesafe.play" %% "play-test" % Common.playVersion % "provided")
        case _ => fail("Invalid scala version")
      }) ++ Seq(
        "org.scalacheck" %% "scalacheck" % Common.scalaCheckVersion,
        "com.fasterxml.jackson.core" % "jackson-annotations" % Common.jacksonVersion,
        "com.fasterxml.jackson.core" % "jackson-core" % Common.jacksonVersion,
        "com.fasterxml.jackson.core" % "jackson-databind" % Common.jacksonVersion,
        "com.fasterxml.jackson.module" %% "jackson-module-scala" % Common.jacksonVersion,
        "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % Common.jacksonVersion
      )
  )
  .dependsOn(jsonSchemaGen, jsonSchemaJackson)

lazy val playExample = project.in(file("examples/play-scala"))

Common.settings
