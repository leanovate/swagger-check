package de.leanovate.swaggercheck.shrinkable

import org.scalacheck.Shrink
import org.scalatest.{MustMatchers, WordSpec}

class CheckJsBooleanSpec extends WordSpec with MustMatchers {
  "JsBoolean" should {
    "not shrink" in {
      val original = CheckJsBoolean(true)

      val shrink = Shrink.shrink(original)

      shrink mustBe empty
    }
  }
}
