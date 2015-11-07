package de.leanovate.swaggercheck.model

import org.scalacheck.Shrink
import org.scalatest.{MustMatchers, WordSpec}

class JsIntegerSpec extends WordSpec with MustMatchers {
  "JsInteger" should {
    "not shrink 0" in {
      val original = JsInteger(None, None, 0)

      val shrink = Shrink.shrink(original)

      shrink mustBe empty
    }

    "not shrink beneath min" in {
      val original = JsInteger(Some(1234), None, 1234)

      val shrink = Shrink.shrink(original)

      shrink mustBe empty
    }

    "not shrink over max" in {
      val original = JsInteger(Some(-1234), None, -1234)

      val shrink = Shrink.shrink(original)

      shrink mustBe empty
    }
  }
}
