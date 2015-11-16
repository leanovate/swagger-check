package de.leanovate.swaggercheck.schema.gen.formats

import de.leanovate.swaggercheck.schema.model.formats.NumberFormats
import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}
import org.scalacheck.{Arbitrary, Gen}

object GeneratableNumberFormats {

  object FloatNumber extends GeneratableFormat[BigDecimal] {
    override def generate: Gen[BigDecimal] = Arbitrary.arbitrary[Float].map(_.toDouble).map(BigDecimal.decimal)

    override def validate(path: JsonPath, value: BigDecimal): ValidationResult =
      NumberFormats.FloatNumber.validate(path, value)
  }

  object DoubleNumber extends GeneratableFormat[BigDecimal] {
    override def generate: Gen[BigDecimal] = Arbitrary.arbitrary[Double].map(BigDecimal.decimal)

    override def validate(path: JsonPath, value: BigDecimal): ValidationResult =
      NumberFormats.DoubleNumber.validate(path, value)
  }

  val defaultFormats = Map(
    "float" -> FloatNumber,
    "double" -> DoubleNumber
  )
}
