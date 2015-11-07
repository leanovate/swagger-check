package de.leanovate.swaggercheck.model

import org.scalacheck.Shrink
import org.scalatest.{MustMatchers, WordSpec}

class CheckJsIntegerSpec extends WordSpec with MustMatchers {
  "JsInteger" should {
    "not shrink 0" in {
      val original = CheckJsInteger(None, None, 0)

      val shrink = Shrink.shrink(original)

      shrink mustBe empty
    }

    "not shrink beneath min" in {
      val original = CheckJsInteger(Some(1234), None, 1234)

      val shrink = Shrink.shrink(original)

      shrink mustBe empty
    }

    "not shrink over max" in {
      val original = CheckJsInteger(Some(-1234), None, -1234)

      val shrink = Shrink.shrink(original)

      shrink mustBe empty
    }
  }
}
