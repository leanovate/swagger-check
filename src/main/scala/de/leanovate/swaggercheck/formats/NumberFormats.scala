package de.leanovate.swaggercheck.formats

import de.leanovate.swaggercheck.VerifyResult
import org.scalacheck.{Arbitrary, Gen}

object NumberFormats {

  object FloatNumber extends Format[BigDecimal] {
    override def generate: Gen[BigDecimal] = Arbitrary.arbitrary[Float].map(BigDecimal.decimal)

    override def verify(path: String, value: BigDecimal): VerifyResult = {
      if (value.isDecimalDouble && value >= BigDecimal.decimal(Float.MinValue) && value <= BigDecimal.decimal(Float.MaxValue))
        // We have to be somewhat lenient here, most implementation do not produce valid float decimals
        VerifyResult.success
      else {
        val parsed = BigDecimal.decimal(value.toFloat)
        VerifyResult.error(s"$value is not a float ($value != $parsed): $path")
      }
    }
  }

  object DoubleNumber extends Format[BigDecimal] {
    override def generate: Gen[BigDecimal] = Arbitrary.arbitrary[Double].map(BigDecimal.decimal)

    override def verify(path: String, value: BigDecimal): VerifyResult =
      if (value.isDecimalDouble)
        VerifyResult.success
      else {
        val parsed = BigDecimal.decimal(value.toDouble)
        VerifyResult.error(s"$value is not a double ($value != $parsed): $path")
      }
  }

  val defaultFormats = Map(
    "float" -> FloatNumber,
    "double" -> DoubleNumber
  )
}
