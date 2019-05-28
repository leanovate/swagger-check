name := "swagger-check-core"

Common.settings

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-test" % Common.playVersion27 % "provided",
  "org.scalacheck" %% "scalacheck" % Common.scalaCheckVersion,
  "com.fasterxml.jackson.core" % "jackson-annotations" % Common.jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-core" % Common.jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % Common.jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % Common.jacksonVersion,
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % Common.jacksonVersion
)
