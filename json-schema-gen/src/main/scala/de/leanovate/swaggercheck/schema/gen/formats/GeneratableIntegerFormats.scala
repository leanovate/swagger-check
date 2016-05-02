package de.leanovate.swaggercheck.schema.gen.formats

import de.leanovate.swaggercheck.schema.model.formats.IntegerFormats
import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}
import org.scalacheck.{Arbitrary, Gen}

object GeneratableIntegerFormats {

  object Int32 extends GeneratableFormat[BigInt] {
    override def generate: Gen[BigInt] = Arbitrary.arbitrary[Int].map(BigInt.apply)

    override def validate(path: JsonPath, value: BigInt): ValidationResult =
      IntegerFormats.Int32.validate(path, value)
  }

  object Int64 extends GeneratableFormat[BigInt] {
    override def generate: Gen[BigInt] = Arbitrary.arbitrary[Long].map(BigInt.apply)

    override def validate(path: JsonPath, value: BigInt): ValidationResult =
      IntegerFormats.Int64.validate(path, value)
  }

  val defaultFormats = Map(
    "int32" -> Int32,
    "int64" -> Int64
  )
}
