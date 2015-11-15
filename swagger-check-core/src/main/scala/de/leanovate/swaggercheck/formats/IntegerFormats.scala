package de.leanovate.swaggercheck.formats

import de.leanovate.swaggercheck.schema.gen.formats.GeneratableFormat
import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}
import org.scalacheck.Gen

object IntegerFormats {

  object Int32 extends GeneratableFormat[BigInt] {
    override def generate: Gen[BigInt] = Gen.choose(Int.MinValue, Int.MaxValue).map(BigInt.apply)

    override def validate(path: JsonPath, value: BigInt): ValidationResult =
      if (value.isValidInt)
        ValidationResult.success
      else
        ValidationResult.error(s"$value is not an int32: $path")
  }

  object Int64 extends GeneratableFormat[BigInt] {
    override def generate: Gen[BigInt] = Gen.choose(Long.MinValue, Long.MaxValue).map(BigInt.apply)

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
