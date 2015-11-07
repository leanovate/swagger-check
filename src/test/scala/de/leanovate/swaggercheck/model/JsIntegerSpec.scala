package de.leanovate.swaggercheck.model

import org.scalacheck.Shrink
import org.scalatest.{MustMatchers, WordSpec}

class JsIntegerSpec extends WordSpec with MustMatchers {
  "JsInteger" should {
    "shrink without min" in {
      val original = JsInteger(None, 12345678)

      val shrink = Shrink.shrink(original)

      shrink must not be empty
      shrink.foreach {
        value =>
          value.min mustBe empty
          value.value must be < BigInt(12345678)
      }
    }
  }
}
