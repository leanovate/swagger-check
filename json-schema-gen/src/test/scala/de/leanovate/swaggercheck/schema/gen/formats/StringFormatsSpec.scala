package de.leanovate.swaggercheck.schema.gen.formats

import de.leanovate.swaggercheck.schema.model.JsonPath
import org.scalatest.{MustMatchers, WordSpec}

class StringFormatsSpec extends WordSpec with MustMatchers {
  "URL string format" should {
    val format = GeneratableStringFormats.defaultFormats("url")

    "be valid for urls" in {
      format.validate(JsonPath(), "http://localhost/something").isSuccess mustBe true
      format.validate(JsonPath(), "http://localhost:8080/something?query=param").isSuccess mustBe true
    }

    "fail for non-urls" in {
      format.validate(JsonPath(), "something").isSuccess mustBe false
    }
  }

  "URI string format" should {
    val format = GeneratableStringFormats.defaultFormats("uri")
    
    "be valid for uris" in {
      format.validate(JsonPath(), "/something").isSuccess mustBe true
      format.validate(JsonPath(), "http://localhost:8080/something?query=param").isSuccess mustBe true
    }

    "fail for non-uris" in {
      format.validate(JsonPath(), ":?something").isSuccess mustBe false
    }
  }

  "UUID string format" should {
    val format = GeneratableStringFormats.defaultFormats("uuid")
    
    "be valid for uuids" in {
      format.validate(JsonPath(), "2df6e079-4028-4aa5-9bdb-bb59a314cdad").isSuccess mustBe true
      format.validate(JsonPath(), "864C67DF-51BB-4688-8A5B-105EC5FDD1D2").isSuccess mustBe true
    }

    "fail for non-uuids" in {
      format.validate(JsonPath(), "864C67DF-51BB-4688").isSuccess mustBe false
    }
  }

  "Email string format" should {
    val format = GeneratableStringFormats.defaultFormats("email")
    
    "be valid for emails" in {
      format.validate(JsonPath(), "someone@on.earth.com").isSuccess mustBe true
      format.validate(JsonPath(), "cheraldine.zakalwe@culture.space").isSuccess mustBe true
    }

    "fail for non-emails" in {
      format.validate(JsonPath(), "someone").isSuccess mustBe false
    }
  }

  "Date string format" should {
    val format = GeneratableStringFormats.defaultFormats("date")
    
    "be valid for dates" in {
      format.validate(JsonPath(), "1856-12-20").isSuccess mustBe true
      format.validate(JsonPath(), "2320-01-30").isSuccess mustBe true
    }

    "fail for non-dates" in {
      format.validate(JsonPath(), "23200130").isSuccess mustBe false
      format.validate(JsonPath(), "2320-01-50").isSuccess mustBe false
    }
  }

  "DateTime string format" should {
    val format = GeneratableStringFormats.defaultFormats("date-time")
    
    "be valid for datetimes" in {
      format.validate(JsonPath(), "1856-12-20T12:34:56").isSuccess mustBe true
      format.validate(JsonPath(), "2320-01-30T12:34:56.123").isSuccess mustBe true
      format.validate(JsonPath(), "1856-12-20T12:34:56Z").isSuccess mustBe true
      format.validate(JsonPath(), "2320-01-30T12:34:56.123Z").isSuccess mustBe true
      format.validate(JsonPath(), "1856-12-20T12:34:56+01:00").isSuccess mustBe true
      format.validate(JsonPath(), "2320-01-30T12:34:56.123+01:00").isSuccess mustBe true
    }

    "fail for non-datetimes" in {
      format.validate(JsonPath(), "2320013012:34:56").isSuccess mustBe false
      format.validate(JsonPath(), "2320-01-5012:34:56").isSuccess mustBe false
    }
  }
}
