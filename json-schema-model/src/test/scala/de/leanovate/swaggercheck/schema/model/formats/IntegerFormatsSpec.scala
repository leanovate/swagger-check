package de.leanovate.swaggercheck.schema.model.formats

import de.leanovate.swaggercheck.schema.model.JsonPath
import org.scalatest.{MustMatchers, WordSpec}

class IntegerFormatsSpec extends WordSpec with MustMatchers {
  "Int32 format" should {
    val format = IntegerFormats.defaultFormats("int32")

    "fail for numbers out of 32-bit range" in {
      format.validate(JsonPath(), BigInt(Int.MaxValue) + BigInt(1)).isSuccess mustBe false
      format.validate(JsonPath(), BigInt(Int.MaxValue)).isSuccess mustBe true
      format.validate(JsonPath(), BigInt(Int.MinValue) - BigInt(1)).isSuccess mustBe false
      format.validate(JsonPath(), BigInt(Int.MinValue)).isSuccess mustBe true
    }
  }

  "Int64 format" should {
    val format = IntegerFormats.defaultFormats("int64")

    "fail for numbers out of 64-bit range" in {
      format.validate(JsonPath(), BigInt(Long.MaxValue) + BigInt(1)).isSuccess mustBe false
      format.validate(JsonPath(), BigInt(Long.MaxValue)).isSuccess mustBe true
      format.validate(JsonPath(), BigInt(Long.MinValue) - BigInt(1)).isSuccess mustBe false
      format.validate(JsonPath(), BigInt(Long.MinValue)).isSuccess mustBe true
    }
  }
}
