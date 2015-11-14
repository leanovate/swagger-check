package de.leanovate.swaggercheck.model

import org.scalacheck.Shrink
import org.scalatest.{MustMatchers, WordSpec}

class CheckJsArraySpec extends WordSpec with MustMatchers {
  "JsArray" should {
    "shrink without min size" in {
      val original = CheckJsArray(None, Seq(
        CheckJsInteger(None, None, 1000000),
        CheckJsString.unformatted("0123456789abcdefghijklmnopqrstuvwxyz"),
        CheckJsBoolean(true),
        CheckJsBoolean(false),
        CheckJsInteger(None, None, 10000),
        CheckJsString.unformatted("zyxwvutsrqponmlkjihgfedcba9876543210")
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
      val original = CheckJsArray(Some(4), Seq(
        CheckJsInteger(None, None, 1000000),
        CheckJsString.unformatted( "0123456789abcdefghijklmnopqrstuvwxyz"),
        CheckJsBoolean(true),
        CheckJsBoolean(false),
        CheckJsInteger(None, None, 10000),
        CheckJsString.unformatted("zyxwvutsrqponmlkjihgfedcba9876543210")
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
      val original = CheckJsArray(Some(6), Seq(
        CheckJsInteger(None, None, 12345678),
        CheckJsString.unformatted("0123456789abcdefghijklmnopqrstuvwxyz"),
        CheckJsBoolean(true),
        CheckJsBoolean(false),
        CheckJsInteger(None, None, 87654321),
        CheckJsString.unformatted("zyxwvutsrqponmlkjihgfedcba9876543210")
      ))
      val originalJson = original.minified

      val shrink = Shrink.shrink(original)

      shrink mustBe empty
    }
  }
}
