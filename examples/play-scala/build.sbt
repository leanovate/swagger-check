name := "play-scala-example"

version := "1.0-SNAPSHOT"

enablePlugins(PlayScala)

scalaVersion := "2.11.8"

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "de.leanovate.swaggercheck" %% "swagger-check-core" % "0.99.4" % Test,
  "org.specs2" %% "specs2-scalacheck" % "3.8.8" % Test,
  specs2 % Test
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
