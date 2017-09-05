name := "json-schema-gen"

Common.settings

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % Common.scalaCheckVersion,
  "com.fasterxml.jackson.core" % "jackson-core" % Common.jacksonVersion
)