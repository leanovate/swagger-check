package de.leanovate.swaggercheck.formats

import de.leanovate.swaggercheck.schema.gen.formats.GeneratableFormat
import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}
import org.scalacheck.{Arbitrary, Gen}

object NumberFormats {

  object FloatNumber extends GeneratableFormat[BigDecimal] {
    override def generate: Gen[BigDecimal] = Arbitrary.arbitrary[Float].map(_.toDouble).map(BigDecimal.decimal)

    override def validate(path: JsonPath, value: BigDecimal): ValidationResult = {
      if (value.isDecimalDouble && value >= BigDecimal.decimal(Float.MinValue) && value <= BigDecimal.decimal(Float.MaxValue))
        // We have to be somewhat lenient here, most implementation do not produce valid float decimals
        ValidationResult.success
      else {
        val parsed = BigDecimal.decimal(value.toFloat)
        ValidationResult.error(s"$value is not a float ($value != $parsed): $path")
      }
    }
  }

  object DoubleNumber extends GeneratableFormat[BigDecimal] {
    override def generate: Gen[BigDecimal] = Arbitrary.arbitrary[Double].map(BigDecimal.decimal)

    override def validate(path: JsonPath, value: BigDecimal): ValidationResult =
      if (value.isDecimalDouble)
        ValidationResult.success
      else {
        val parsed = BigDecimal.decimal(value.toDouble)
        ValidationResult.error(s"$value is not a double ($value != $parsed): $path")
      }
  }

  val defaultFormats = Map(
    "float" -> FloatNumber,
    "double" -> DoubleNumber
  )
}
