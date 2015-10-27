package de.leanovate.swaggercheck.formats

import java.net.{URI, URL}
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID

import de.leanovate.swaggercheck.{Generators, VerifyResult}
import org.scalacheck.Gen

import scala.util.Try

object StringFormats {

  object URLString extends Format[String] {
    override def generate: Gen[String] = Generators.url

    override def verify(path: String, value: String): VerifyResult =
      if (Try(new URL(value)).isSuccess)
        VerifyResult.success
      else
        VerifyResult.error(s"'$value' is not an url: $path")
  }

  object URIString extends Format[String] {
    override def generate: Gen[String] = Generators.uri

    override def verify(path: String, value: String): VerifyResult =
      if (Try(new URI(value)).isSuccess)
        VerifyResult.success
      else
        VerifyResult.error(s"'$value' is not an uri: $path")
  }

  object UUIDString extends Format[String] {
    override def generate: Gen[String] =
      Gen.uuid.map(_.toString)

    override def verify(path: String, value: String): VerifyResult =
      if (Try(UUID.fromString(value)).isSuccess)
        VerifyResult.success
      else
        VerifyResult.error(s"'$value' is not an uri: $path")
  }

  object EmailString extends Format[String] {
    val emailPattern = "^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$".r

    override def generate: Gen[String] = Generators.email

    override def verify(path: String, value: String): VerifyResult =
      if (emailPattern.pattern.matcher(value).matches()) {
        VerifyResult.success
      } else {
        VerifyResult.error(s"'$value' is not an email: $path")
      }  }

  object DateString extends Format[String] {
    override def generate: Gen[String] = {
      Gen.choose[Long](Long.MinValue, Long.MaxValue).map {
        diff: Long =>
          val instant = Instant.now().plus(diff, ChronoUnit.NANOS)
          DateTimeFormatter.ISO_DATE.format(instant)
      }
    }

    override def verify(path: String, value: String): VerifyResult =
      if (Try(DateTimeFormatter.ISO_DATE.parse(value)).isSuccess)
        VerifyResult.success
      else
        VerifyResult.error(s"'$value' is not a date: $path")
  }

  object DateTimeString extends Format[String] {
    override def generate: Gen[String] = {
      Gen.choose[Long](Long.MinValue, Long.MaxValue).map {
        diff: Long =>
          val instant = Instant.now().plus(diff, ChronoUnit.NANOS)
          DateTimeFormatter.ISO_INSTANT.format(instant)
      }
    }

    override def verify(path: String, value: String): VerifyResult =
      if (Try(DateTimeFormatter.ISO_DATE_TIME.parse(value)).isSuccess)
        VerifyResult.success
      else
        VerifyResult.error(s"'$value' is not a date-time: $path")
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
