package de.leanovate.swaggercheck.model

import org.scalacheck.Shrink
import org.scalatest.{MustMatchers, WordSpec}

class JsObjectSpec extends WordSpec with MustMatchers {
  "JsObject" should {
    "shrink without order or required" in {
      val original = JsObject(Set.empty, None, Map(
        "one" -> JsInteger(None, None, 1000000),
        "two" -> JsUnformattedString(None, "0123456789abcdefghijklmnopqrstuvwxyz"),
        "three" -> JsBoolean(true),
        "four" -> JsBoolean(false),
        "five" ->JsInteger(None, None, 10000),
        "six" -> JsUnformattedString(None, "zyxwvutsrqponmlkjihgfedcba9876543210")
      ))
      val originalJson = original.minified

      val shrink = Shrink.shrink[JsObject](original)

      shrink must not be empty
      shrink.foreach {
        value =>
          value.minified.length must be < originalJson.length
      }
    }
  }
}
