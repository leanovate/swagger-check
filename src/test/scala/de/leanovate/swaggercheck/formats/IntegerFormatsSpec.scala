package de.leanovate.swaggercheck.formats

import org.scalatest.{MustMatchers, WordSpec}

class IntegerFormatsSpec extends WordSpec with MustMatchers {
  "Int32 format" should {
    "fail for numbers out of 32-bit range" in {
      IntegerFormats.Int32.verify("", BigInt(Int.MaxValue) + BigInt(1)).isSuccess mustBe false
      IntegerFormats.Int32.verify("", BigInt(Int.MaxValue)).isSuccess mustBe true
      IntegerFormats.Int32.verify("", BigInt(Int.MinValue) - BigInt(1)).isSuccess mustBe false
      IntegerFormats.Int32.verify("", BigInt(Int.MinValue)).isSuccess mustBe true
    }
  }

  "Int64 format" should {
    "fail for numbers out of 64-bit range" in {
      IntegerFormats.Int64.verify("", BigInt(Long.MaxValue) + BigInt(1)).isSuccess mustBe false
      IntegerFormats.Int64.verify("", BigInt(Long.MaxValue)).isSuccess mustBe true
      IntegerFormats.Int64.verify("", BigInt(Long.MinValue) - BigInt(1)).isSuccess mustBe false
      IntegerFormats.Int64.verify("", BigInt(Long.MinValue)).isSuccess mustBe true
    }
  }
}
