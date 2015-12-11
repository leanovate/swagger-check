package de.leanovate.swaggercheck.schema.model.formats

import java.net.{URI, URL}
import java.time.format.DateTimeFormatter
import java.util.UUID

import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}

import scala.util.Try

object StringFormats {

  object URLString extends ValueFormat[String] {
    override def validate(path: JsonPath, value: String): ValidationResult[String] =
      if (Try(new URL(value)).isSuccess)
        ValidationResult.success(value)
      else
        ValidationResult.error(s"'$value' is not an url: $path")
  }

  object URIString extends ValueFormat[String] {
    override def validate(path: JsonPath, value: String): ValidationResult[String] =
      if (Try(new URI(value)).isSuccess)
        ValidationResult.success(value)
      else
        ValidationResult.error(s"'$value' is not an uri: $path")
  }

  object UUIDString extends ValueFormat[String] {
    override def validate(path: JsonPath, value: String): ValidationResult[String] =
      if (Try(UUID.fromString(value)).isSuccess)
        ValidationResult.success(value)
      else
        ValidationResult.error(s"'$value' is not an uuid: $path")
  }

  object EmailString extends ValueFormat[String] {
    val emailPattern = """(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])""".r

    override def validate(path: JsonPath, value: String): ValidationResult[String] =
      if (emailPattern.pattern.matcher(value).matches()) {
        ValidationResult.success(value)
      } else {
        ValidationResult.error(s"'$value' is not an email: $path")
      }  }

  object DateString extends ValueFormat[String] {
    override def validate(path: JsonPath, value: String): ValidationResult[String] =
      if (Try(DateTimeFormatter.ISO_DATE.parse(value)).isSuccess)
        ValidationResult.success(value)
      else
        ValidationResult.error(s"'$value' is not a date: $path")
  }

  object DateTimeString extends ValueFormat[String] {
    override def validate(path: JsonPath, value: String): ValidationResult[String] =
      if (Try(DateTimeFormatter.ISO_DATE_TIME.parse(value)).isSuccess)
        ValidationResult.success(value)
      else
        ValidationResult.error(s"'$value' is not a date-time: $path")
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
