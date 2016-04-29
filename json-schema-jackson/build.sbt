name := "json-schema-jackson"

Common.settings

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-annotations" % Common.jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-core" % Common.jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % Common.jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % Common.jacksonVersion
)