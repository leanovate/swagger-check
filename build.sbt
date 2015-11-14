import sbt.Keys._

name := "swagger-check"

lazy val root = project.in(file(".")).settings(publishArtifact := false)
  .aggregate(jsonSchemaModel, jsonSchemaGen, jsonSchemaPlay, jsonSchemaJackson, swaggerCheckCore)

lazy val jsonSchemaModel = project.in(file("json-schema-model"))

lazy val jsonSchemaGen = project.in(file("json-schema-gen")).dependsOn(jsonSchemaModel)

lazy val jsonSchemaPlay = project.in(file("json-schema-play"))

lazy val jsonSchemaJackson = project.in(file("json-schema-jackson"))

lazy val swaggerCheckCore = project.in(file("swagger-check-core")).dependsOn(jsonSchemaGen)

Common.settings
