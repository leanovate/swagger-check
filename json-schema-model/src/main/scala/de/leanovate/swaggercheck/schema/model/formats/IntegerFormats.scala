package de.leanovate.swaggercheck.schema.model.formats

import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}

object IntegerFormats {

  object Int32 extends ValueFormat[BigInt] {
    override def validate(path: JsonPath, value: BigInt): ValidationResult =
      if (value.isValidInt)
        ValidationResult.success
      else
        ValidationResult.error(s"$value is not an int32: $path")
  }

  object Int64 extends ValueFormat[BigInt] {
    override def validate(path: JsonPath, value: BigInt): ValidationResult =
      if (value.isValidLong)
        ValidationResult.success
      else
        ValidationResult.error(s"$value is not an int64: $path")
  }

  val defaultFormats = Map(
    "int32" -> Int32,
    "int64" -> Int64
  )
}
