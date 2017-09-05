name := "play-scala-example"

version := "1.0-SNAPSHOT"

enablePlugins(PlayScala)

scalaVersion := "2.12.3"

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "de.leanovate.swaggercheck" %% "swagger-check-core" % System.getProperty("core.version", "1.0.0-SNAPSHOT") % Test,
  "org.specs2" %% "specs2-scalacheck" % "3.9.5" % Test,
  specs2 % Test
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
