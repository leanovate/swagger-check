package de.leanovate.swaggercheck.formats

import org.scalacheck.Gen

object IntegerFormats {

  object Int32 extends Format[Long] {
    override def generate: Gen[Long] = Gen.choose(Int.MinValue, Int.MaxValue)
  }

  object Int64 extends Format[Long] {
    override def generate: Gen[Long] = Gen.choose(Long.MinValue, Long.MaxValue)
  }

  val defaultFormats = Map(
    "int32" -> Int32,
    "int64" -> Int64
  )
}
