package de.leanovate.swaggercheck.formats

import org.scalatest.{MustMatchers, WordSpec}

class NumberFormatsSpec extends WordSpec with MustMatchers {
  "Float format" should {
    "fail for numbers out of range" in {
      NumberFormats.FloatNumber.verify("", BigDecimal.decimal(Float.MaxValue) + BigDecimal(1)).isSuccess mustBe false
      NumberFormats.FloatNumber.verify("", BigDecimal.decimal(Float.MaxValue)).isSuccess mustBe true
      NumberFormats.FloatNumber.verify("", BigDecimal.decimal(Float.MinValue) - BigDecimal(1)).isSuccess mustBe false
      NumberFormats.FloatNumber.verify("", BigDecimal.decimal(Float.MinValue)).isSuccess mustBe true
    }
  }

  "Double format" should {
    "fail for numbers out of range" in {
      NumberFormats.DoubleNumber.verify("", BigDecimal.decimal(Double.MaxValue) + BigDecimal(1)).isSuccess mustBe false
      NumberFormats.DoubleNumber.verify("", BigDecimal.decimal(Double.MaxValue)).isSuccess mustBe true
      NumberFormats.DoubleNumber.verify("", BigDecimal.decimal(Double.MinValue) - BigDecimal(1)).isSuccess mustBe false
      NumberFormats.DoubleNumber.verify("", BigDecimal.decimal(Double.MinValue)).isSuccess mustBe true
    }
  }
}
