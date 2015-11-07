package de.leanovate.swaggercheck.model

import org.scalacheck.Shrink
import org.scalatest.{MustMatchers, WordSpec}

class JsBooleanSpec extends WordSpec with MustMatchers {
  "JsBoolean" should {
    "not shrink" in {
      val original = JsBoolean(true)

      val shrink = Shrink.shrink(original)

      shrink mustBe empty
    }
  }
}
