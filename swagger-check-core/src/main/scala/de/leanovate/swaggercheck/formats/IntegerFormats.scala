package de.leanovate.swaggercheck.formats

import de.leanovate.swaggercheck.VerifyResult
import org.scalacheck.Gen

object IntegerFormats {

  object Int32 extends Format[BigInt] {
    override def generate: Gen[BigInt] = Gen.choose(Int.MinValue, Int.MaxValue).map(BigInt.apply)

    override def verify(path: String, value: BigInt): VerifyResult =
      if (value.isValidInt)
        VerifyResult.success
      else
        VerifyResult.error(s"$value is not an int32: $path")
  }

  object Int64 extends Format[BigInt] {
    override def generate: Gen[BigInt] = Gen.choose(Long.MinValue, Long.MaxValue).map(BigInt.apply)

    override def verify(path: String, value: BigInt): VerifyResult =
      if (value.isValidLong)
        VerifyResult.success
      else
        VerifyResult.error(s"$value is not an int64: $path")
  }

  val defaultFormats = Map(
    "int32" -> Int32,
    "int64" -> Int64
  )
}
