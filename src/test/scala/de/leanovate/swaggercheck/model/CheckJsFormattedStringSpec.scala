package de.leanovate.swaggercheck.model

import org.scalacheck.Shrink
import org.scalatest.{MustMatchers, WordSpec}

class CheckJsFormattedStringSpec extends WordSpec with MustMatchers {
  "JsFormattedString" should {
    "not shrink" in {
      val original = CheckJsString.formatted("0123456789abcdefghijklmnopqrstuvxyz")

      val shrink = Shrink.shrink(original)

      shrink mustBe empty
    }
  }
}
