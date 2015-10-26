package de.leanovate.swaggercheck.formats

import org.scalacheck.Gen

object NumberFormats {

  object FloatNumber extends Format[Double] {
    override def generate: Gen[Double] = Gen.choose(Float.MinValue, Float.MaxValue)
  }

  object DoubleNumber extends Format[Double] {
    override def generate: Gen[Double] = Gen.choose(Double.MinValue, Double.MaxValue)
  }

  val defaultFormats = Map(
    "float" -> FloatNumber,
    "double" -> DoubleNumber
  )
}
