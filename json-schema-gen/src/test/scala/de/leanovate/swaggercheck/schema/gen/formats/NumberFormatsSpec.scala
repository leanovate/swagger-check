package de.leanovate.swaggercheck.schema.gen.formats

import de.leanovate.swaggercheck.schema.model.JsonPath
import org.scalatest.{MustMatchers, WordSpec}

class NumberFormatsSpec extends WordSpec with MustMatchers {
  "Float format" should {
    "fail for numbers out of range" in {
      NumberFormats.FloatNumber.validate(JsonPath(), BigDecimal.decimal(Float.MaxValue) + BigDecimal(1)).isSuccess mustBe false
      NumberFormats.FloatNumber.validate(JsonPath(), BigDecimal.decimal(Float.MaxValue)).isSuccess mustBe true
      NumberFormats.FloatNumber.validate(JsonPath(), BigDecimal.decimal(Float.MinValue) - BigDecimal(1)).isSuccess mustBe false
      NumberFormats.FloatNumber.validate(JsonPath(), BigDecimal.decimal(Float.MinValue)).isSuccess mustBe true
    }
  }

  "Double format" should {
    "fail for numbers out of range" in {
      NumberFormats.DoubleNumber.validate(JsonPath(), BigDecimal.decimal(Double.MaxValue) + BigDecimal(1)).isSuccess mustBe false
      NumberFormats.DoubleNumber.validate(JsonPath(), BigDecimal.decimal(Double.MaxValue)).isSuccess mustBe true
      NumberFormats.DoubleNumber.validate(JsonPath(), BigDecimal.decimal(Double.MinValue) - BigDecimal(1)).isSuccess mustBe false
      NumberFormats.DoubleNumber.validate(JsonPath(), BigDecimal.decimal(Double.MinValue)).isSuccess mustBe true
    }
  }
}
