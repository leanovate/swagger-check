package de.leanovate.swaggercheck.formats

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

import de.leanovate.swaggercheck.Generators
import org.scalacheck.Gen

object StringFormats {

  object URL extends Format[String] {
    override def generate: Gen[String] = Generators.url
  }

  object URI extends Format[String] {
    override def generate: Gen[String] = Generators.uri
  }

  object UUID extends Format[String] {
    override def generate: Gen[String] =
      Gen.uuid.map(_.toString)
  }

  object Email extends Format[String] {
    override def generate: Gen[String] = Generators.email
  }

  object Date extends Format[String] {
    override def generate: Gen[String] = {
      Gen.choose[Long](Long.MinValue, Long.MaxValue).map {
        diff: Long =>
          val instant = Instant.now().plus(diff, ChronoUnit.NANOS)
          DateTimeFormatter.ISO_DATE.format(instant)
      }
    }
  }

  object DateTime extends Format[String] {
    override def generate: Gen[String] = {
      Gen.choose[Long](Long.MinValue, Long.MaxValue).map {
        diff: Long =>
          val instant = Instant.now().plus(diff, ChronoUnit.NANOS)
          DateTimeFormatter.ISO_INSTANT.format(instant)
      }
    }
  }

  val defaultFormats = Map(
    "url" -> URL,
    "uri" -> URI,
    "uuid" -> UUID,
    "email" -> Email,
    "date" -> Date,
    "date-time" -> DateTime
  )
}
