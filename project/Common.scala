import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import xerial.sbt.Sonatype.SonatypeKeys._

object Common {
  val scalaCheckVersion = "1.13.4"

  val jacksonVersion = "2.8.6"

  val playVersion = "2.5.12"

  val settings = Seq(

    organization := "de.leanovate.swaggercheck",

    sonatypeProfileName := "de.leanovate",

    scalaVersion := "2.11.8",

    scalacOptions := Seq("-deprecation", "-feature"),

    fork in run := true,

    fork in Test := true,

    testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-v", "2", "-w", "1", "-x", "10"),

    publishMavenStyle := true,

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "org.mockito" % "mockito-core" % "2.7.10" % "test"
    ),

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
    },

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
  )
}