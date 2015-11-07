package de.leanovate.swaggercheck.model

import org.scalacheck.Shrink
import org.scalatest.{MustMatchers, WordSpec}

class JsArraySpec extends WordSpec with MustMatchers {
  "JsArray" should {
    "shrink without min size" in {
      val original = JsArray(None, Seq(
        JsInteger(None, None, 1000000),
        JsUnformattedString(None, "0123456789abcdefghijklmnopqrstuvwxyz"),
        JsBoolean(true),
        JsBoolean(false),
        JsInteger(None, None, 10000),
        JsUnformattedString(None, "zyxwvutsrqponmlkjihgfedcba9876543210")
      ))
      val originalJson = original.minified

      val shrink = Shrink.shrink(original)

      shrink must not be empty
      shrink.foreach {
        value =>
          value.minSize mustBe empty
          value.elements.length must be <= 6
          value.minified.length must be < originalJson.length
      }
    }

    "shrink with min size" in {
      val original = JsArray(Some(4), Seq(
        JsInteger(None, None, 1000000),
        JsUnformattedString(None, "0123456789abcdefghijklmnopqrstuvwxyz"),
        JsBoolean(true),
        JsBoolean(false),
        JsInteger(None, None, 10000),
        JsUnformattedString(None, "zyxwvutsrqponmlkjihgfedcba9876543210")
      ))
      val originalJson = original.minified

      val shrink = Shrink.shrink(original)

      shrink must not be empty
      shrink.foreach {
        value =>
          value.minSize mustBe Some(4)
          value.elements.length must be <= 6
          value.elements.length must be >= 4
          value.minified.length must be < originalJson.length
      }
    }

    "not shrink beneath min size" in {
      val original = JsArray(Some(6), Seq(
        JsInteger(None, None, 12345678),
        JsUnformattedString(None, "0123456789abcdefghijklmnopqrstuvwxyz"),
        JsBoolean(true),
        JsBoolean(false),
        JsInteger(None, None, 87654321),
        JsUnformattedString(None, "zyxwvutsrqponmlkjihgfedcba9876543210")
      ))
      val originalJson = original.minified

      val shrink = Shrink.shrink(original)

      shrink mustBe empty
    }
  }
}
