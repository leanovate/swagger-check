package de.leanovate.swaggercheck.formats

import org.scalatest.{MustMatchers, WordSpec}

class IntegerFormatsSpec extends WordSpec with MustMatchers {
  "Int32 format" should {
    "fail for numbers with fractions" in {
      IntegerFormats.Int32.verify("", BigDecimal.decimal(10.25)).isSuccess mustBe false
      IntegerFormats.Int32.verify("", BigDecimal.decimal(10.0)).isSuccess mustBe true
    }

    "fail for numbers out of 32-bit range" in {
      IntegerFormats.Int32.verify("", BigDecimal(Int.MaxValue) + BigDecimal(1)).isSuccess mustBe false
      IntegerFormats.Int32.verify("", BigDecimal(Int.MaxValue)).isSuccess mustBe true
      IntegerFormats.Int32.verify("", BigDecimal(Int.MinValue) - BigDecimal(1)).isSuccess mustBe false
      IntegerFormats.Int32.verify("", BigDecimal(Int.MinValue)).isSuccess mustBe true
    }
  }

  "Int64 format" should {
    "fail for numbers with fractions" in {
      IntegerFormats.Int64.verify("", BigDecimal.decimal(10.25)).isSuccess mustBe false
      IntegerFormats.Int64.verify("", BigDecimal.decimal(10.0)).isSuccess mustBe true
    }

    "fail for numbers out of 64-bit range" in {
      IntegerFormats.Int64.verify("", BigDecimal(Long.MaxValue) + BigDecimal(1)).isSuccess mustBe false
      IntegerFormats.Int64.verify("", BigDecimal(Long.MaxValue)).isSuccess mustBe true
      IntegerFormats.Int64.verify("", BigDecimal(Long.MinValue) - BigDecimal(1)).isSuccess mustBe false
      IntegerFormats.Int64.verify("", BigDecimal(Long.MinValue)).isSuccess mustBe true
    }
  }
}
