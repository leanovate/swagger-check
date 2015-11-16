package de.leanovate.swaggercheck.schema.model.formats

import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}

object NumberFormats {

  object FloatNumber extends ValueFormat[BigDecimal] {
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

  object DoubleNumber extends ValueFormat[BigDecimal] {
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
