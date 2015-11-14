package de.leanovate.swaggercheck.fixtures.model

import java.net.{URI, URL}
import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDate}
import java.util.UUID

import de.leanovate.swaggercheck.generators.Generators
import org.scalacheck.{Arbitrary, Gen}
import play.api.libs.json.Json

import scala.util.Try

case class AnyThing(
                     anUUID: String,
                     anURL: String,
                     anURI: String,
                     anEmail: String,
                     aDate: LocalDate,
                     aDateTime: Instant,
                     anInt32: Int,
                     anInt64: Long,
                     aFloat: Float,
                     aDouble: Double,
                     aBoolean: Boolean,
                     anEnum: String,
                     aMap: Map[String, String]
                   ) {
  def isValid(): Boolean = {
    Try {
      UUID.fromString(anUUID)
      new URL(anURL)
      new URI(anURI)
    }.isSuccess && Set("V1", "V2", "V3").contains(anEnum)
  }
}

object AnyThing {
  implicit val jsonFormat = Json.format[AnyThing]

  implicit val arbitrary = Arbitrary(for {
    anUUID <- Gen.uuid.map(_.toString)
    anURL <- Generators.url
    anURI <- Generators.uri
    anEmail <- Generators.email
    aDate <- Arbitrary.arbitrary[Int].map(diff => LocalDate.now().plus(diff, ChronoUnit.DAYS))
    aDateTime <- Arbitrary.arbitrary[Long].map(diff => Instant.now().plus(diff, ChronoUnit.NANOS))
    anInt32 <- Arbitrary.arbitrary[Int]
    anInt64 <- Arbitrary.arbitrary[Long]
    aFloat <- Arbitrary.arbitrary[Float]
    aDouble <- Arbitrary.arbitrary[Double]
    aBoolean <- Arbitrary.arbitrary[Boolean]
    anEnum <- Gen.oneOf("V1", "V2", "V3")
    aMap <- Arbitrary.arbitrary[Map[String, String]]
  } yield AnyThing(anUUID, anURL, anURI, anEmail, aDate, aDateTime, anInt32, anInt64, aFloat, aDouble, aBoolean, anEnum, aMap))
}