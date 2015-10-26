import sbt.Keys._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

name := "swagger-check"

organization := "de.leanovate"

scalaVersion := "2.11.7"

val playVersion = "2.4.3"

scalacOptions := Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "io.swagger" % "swagger-parser" % "1.0.10",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scalacheck" %% "scalacheck" % "1.12.5",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.5.4",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.5.4",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.5.4",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.3",
  "com.typesafe.play" %% "play-test" % playVersion % "provided",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.mockito" % "mockito-core" % "1.10.19" % "test"
)

fork in run := true

publishMavenStyle := true

pomExtra := {
  <url>https://github.com/leanovate/swagger-check</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>http://opensource.org/licenses/MIT</url>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:github.com/leanovate/swagger-check</connection>
      <developerConnection>scm:git:git@github.com:/leanovate/swagger-check</developerConnection>
      <url>github.com/leanovate/swagger-check</url>
    </scm>
    <developers>
      <developer>
        <id>untoldwind</id>
        <name>Bodo Junglas</name>
        <url>http://untoldwind.github.io/</url>
      </developer>
    </developers>
}

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)
