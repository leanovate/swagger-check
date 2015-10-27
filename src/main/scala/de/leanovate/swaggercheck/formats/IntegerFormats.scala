package de.leanovate.swaggercheck.formats

import de.leanovate.swaggercheck.VerifyResult
import org.scalacheck.Gen

object IntegerFormats {

  object Int32 extends Format[Long] {
    override def generate: Gen[Long] = Gen.choose(Int.MinValue, Int.MaxValue)

    override def verify(path: String, value: Long): VerifyResult =
      if (value.isValidLong && value >= Int.MinValue && value <= Int.MaxValue)
        VerifyResult.success
      else
        VerifyResult.error(s"$value is not an int32: $path")
  }

  object Int64 extends Format[Long] {
    override def generate: Gen[Long] = Gen.choose(Long.MinValue, Long.MaxValue)

    override def verify(path: String, value: Long): VerifyResult =
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
