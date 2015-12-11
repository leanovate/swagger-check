package de.leanovate.swaggercheck.schema.gen.formats

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDate}

import de.leanovate.swaggercheck.generators.Generators
import de.leanovate.swaggercheck.schema.model.formats.StringFormats
import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}
import org.scalacheck.Gen

object GeneratableStringFormats {

  object URLString extends GeneratableFormat[String] {
    override def generate: Gen[String] = Generators.url

    override def validate(path: JsonPath, value: String): ValidationResult[String] =
      StringFormats.URLString.validate(path, value)
  }

  object URIString extends GeneratableFormat[String] {
    override def generate: Gen[String] = Generators.uri

    override def validate(path: JsonPath, value: String): ValidationResult[String] =
      StringFormats.URIString.validate(path, value)
  }

  object UUIDString extends GeneratableFormat[String] {
    override def generate: Gen[String] =
      Gen.uuid.map(_.toString)

    override def validate(path: JsonPath, value: String): ValidationResult[String] =
      StringFormats.UUIDString.validate(path, value)
  }

  object EmailString extends GeneratableFormat[String] {
    val emailPattern = """(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])""".r

    override def generate: Gen[String] = Generators.email

    override def validate(path: JsonPath, value: String): ValidationResult[String] =
      StringFormats.EmailString.validate(path, value)
  }

  object DateString extends GeneratableFormat[String] {
    override def generate: Gen[String] = {
      Gen.choose[Int](-300000, 300000).map {
        diff: Int =>
          val instant = LocalDate.ofEpochDay(diff)
          DateTimeFormatter.ISO_DATE.format(instant)
      }
    }

    override def validate(path: JsonPath, value: String): ValidationResult[String] =
      StringFormats.DateString.validate(path, value)
  }

  object DateTimeString extends GeneratableFormat[String] {
    override def generate: Gen[String] = {
      Gen.choose[Long](Long.MinValue, Long.MaxValue).map {
        diff: Long =>
          val instant = Instant.now().plus(diff, ChronoUnit.NANOS)
          DateTimeFormatter.ISO_INSTANT.format(instant)
      }
    }

    override def validate(path: JsonPath, value: String): ValidationResult[String] =
      StringFormats.DateTimeString.validate(path, value)
  }

  val defaultFormats = Map(
    "url" -> URLString,
    "uri" -> URIString,
    "uuid" -> UUIDString,
    "email" -> EmailString,
    "date" -> DateString,
    "date-time" -> DateTimeString
  )
}
