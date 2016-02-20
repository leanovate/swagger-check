name := "swagger-check-core"

Common.settings

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scalacheck" %% "scalacheck" % Common.scalaCheckVersion,
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.5.4",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.5.4",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.5.4",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.3",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.4.5",
  "com.typesafe.play" %% "play-test" % Common.playVersion % "provided",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value
)
