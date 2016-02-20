
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import xerial.sbt.Sonatype.SonatypeKeys._

object Common {
  val scalaCheckVersion = "1.13.0"

  val jacksonVersion = "2.5.4"

  val playVersion = "2.4.3"

  val settings = Seq(

    organization := "de.leanovate.swaggercheck",

    sonatypeProfileName := "de.leanovate",

    scalaVersion := "2.11.7",

    scalacOptions := Seq("-deprecation", "-feature"),

    fork in run := true,

    publishMavenStyle := true,

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.mockito" % "mockito-core" % "1.10.19" % "test"
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