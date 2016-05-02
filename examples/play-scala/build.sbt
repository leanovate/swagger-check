name := "play-scala-example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(conflictManager := ConflictManager.latestCompatible)
  .settings(dependencyOverrides := Set(
    "org.scala-lang"              % "scala-library"             % "2.11.8",
    "org.scala-lang"              % "scala-reflect"             % "2.11.8",
    "org.scala-lang.modules"      %% "scala-xml"                % "1.0.1",
    "org.scala-lang.modules"      %% "scala-parser-combinators" % "1.0.4",
    "com.google.guava"            % "guava"                     % "19.0",
    "com.fasterxml.jackson.core"  % "jackson-core"              % "2.7.3",
    "com.fasterxml.jackson.core"  % "jackson-annotations"       % "2.7.3",
    "com.fasterxml.jackson.core"  % "jackson-databind"          % "2.7.3",

    "org.specs2"                  %% "specs2-core"              % "3.7.3"
  ))
  .enablePlugins(PlayScala)

scalaVersion := "2.11.8"

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "de.leanovate.swaggercheck" %% "swagger-check-core" % "0.99.3" % Test,
  "org.specs2" %% "specs2-scalacheck" % "3.7.3" % Test,
  specs2 % Test
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
