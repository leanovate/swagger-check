package de.leanovate.swaggercheck.model

import org.scalatest.{MustMatchers, WordSpec}

class JsValueSpec extends WordSpec with MustMatchers {
  "JsValueDeserializer" should {
    "deserialize null to JsNull" in {
      JsValue.parse("null") mustEqual JsNull
    }

    "deserialize integers to JsInteger" in {
      val result = JsValue.parse("1234")

      result mustBe an[JsInteger]
      result.asInstanceOf[JsInteger].value mustEqual BigInt(1234)
      result.asInstanceOf[JsInteger].min mustEqual Some(BigInt(1234))
    }

    "deserialize floats to JsNumber" in {
      val result = JsValue.parse("1234.5")

      result mustBe an[JsNumber]
      result.asInstanceOf[JsNumber].value mustEqual BigDecimal(1234.5)
      result.asInstanceOf[JsNumber].min mustEqual Some(BigDecimal(1234.5))
    }

    "deserialize strings to JsFormatterString" in {
      val result = JsValue.parse( """"one piece of string"""")

      result mustBe an[JsFormattedString]
      result.asInstanceOf[JsFormattedString].value mustEqual "one piece of string"
    }

    "deserialize booleans to JsBoolean" in {
      JsValue.parse("true") mustEqual JsBoolean(true)
      JsValue.parse("false") mustEqual JsBoolean(false)
    }

    "deserialize arrays to JsArray" in {
      val result = JsValue.parse( """[1234, 1234.5, true, "one piece of string"]""")

      result mustBe an[JsArray]
      result.asInstanceOf[JsArray].elements mustEqual Seq(
        JsInteger.fixed(1234),
        JsNumber.fixed(1234.5),
        JsBoolean(true),
        JsFormattedString("one piece of string")
      )
      result.asInstanceOf[JsArray].minSize mustEqual Some(4)
    }

    "deserialize objects to JsObject" in {
      val result = JsValue.parse( """{"one": 1234, "two": true, "three": "one piece of string"}""")

      result mustBe an[JsObject]
      result.asInstanceOf[JsObject].required mustEqual Set(
        "one", "two", "three"
      )
      result.asInstanceOf[JsObject].fields mustEqual Map(
        "one" -> JsInteger.fixed(1234),
        "two" -> JsBoolean(true),
        "three" -> JsFormattedString("one piece of string")
      )
    }
  }
}
