import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import xerial.sbt.Sonatype.SonatypeKeys._

object Common {
  val scalaCheckVersion = "1.13.4"

  val jacksonVersion = "2.8.6"

  val playVersion = "2.5.12"
  val playVersion26 = "2.6.0"
  val playVersion26RC = "2.6.0-RC2"

  val scalaVersion11 = "2.11.8"
  val scalaVersion12 = "2.12.2"

  val settings = Seq(

    organization := "de.leanovate.swaggercheck",

    sonatypeProfileName := "de.leanovate",

    scalacOptions := Seq("-deprecation", "-feature"),

    fork in run := true,

    fork in Test := true,

    scalaVersion := scalaVersion12,
    crossScalaVersions := Seq(scalaVersion11, scalaVersion12),

    testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-v", "2", "-w", "1", "-x", "10"),

    publishMavenStyle := true,

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "org.mockito" % "mockito-core" % "2.8.47" % "test"
    ),

    releaseCrossBuild := true,

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
      ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
      setNextVersion,
      commitNextVersion,
      ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true),
      pushChanges
    )
  )
}
