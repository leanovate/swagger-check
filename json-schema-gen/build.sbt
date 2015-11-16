name := "json-schema-gen"

Common.settings

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scalacheck" %% "scalacheck" % Common.scalaCheckVersion,
  "com.fasterxml.jackson.core" % "jackson-core" % Common.jacksonVersion
)