package de.leanovate.swaggercheck.model

import org.scalacheck.Shrink
import org.scalatest.{MustMatchers, WordSpec}

class JsFormattedStringSpec extends WordSpec with MustMatchers {
  "JsFormattedString" should {
    "not shrink" in {
      val original = JsFormattedString("0123456789abcdefghijklmnopqrstuvxyz")

      val shrink = Shrink.shrink(original)

      shrink mustBe empty
    }
  }
}
