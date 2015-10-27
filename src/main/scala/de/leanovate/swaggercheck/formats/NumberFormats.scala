package de.leanovate.swaggercheck.formats

import de.leanovate.swaggercheck.VerifyResult
import org.scalacheck.Gen

object NumberFormats {

  object FloatNumber extends Format[Double] {
    override def generate: Gen[Double] = Gen.choose(Float.MinValue, Float.MaxValue)

    override def verify(path: String, value: Double): VerifyResult =
      if (value >= Float.MinValue && value <= Float.MaxValue)
        VerifyResult.success
      else
        VerifyResult.error(s"$value is not an float: $path")
  }

  object DoubleNumber extends Format[Double] {
    override def generate: Gen[Double] = Gen.choose(Double.MinValue, Double.MaxValue)

    override def verify(path: String, value: Double): VerifyResult =
      if (value >= Double.MinValue && value <= Double.MaxValue)
        VerifyResult.success
      else
        VerifyResult.error(s"$value is not an double: $path")
  }

  val defaultFormats = Map(
    "float" -> FloatNumber,
    "double" -> DoubleNumber
  )
}
