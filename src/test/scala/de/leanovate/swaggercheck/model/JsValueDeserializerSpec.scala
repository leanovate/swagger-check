package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.scalatest.{MustMatchers, WordSpec}

class JsValueDeserializerSpec extends WordSpec with MustMatchers {
  val mapper = new ObjectMapper()

  "JsValueDeserializer" should {
    "deserialize null to JsNull" in {
      mapper.readValue("null", classOf[JsValue]) mustEqual JsNull
    }

    "deserialize integers to JsInteger" in {
      val result = mapper.readValue("1234", classOf[JsValue])

      result mustBe an[JsInteger]
      result.asInstanceOf[JsInteger].value mustEqual BigInt(1234)
      result.asInstanceOf[JsInteger].min mustEqual Some(BigInt(1234))
    }

    "deserialize floats to JsNumber" in {
      val result = mapper.readValue("1234.5", classOf[JsValue])

      result mustBe an[JsNumber]
      result.asInstanceOf[JsNumber].value mustEqual BigDecimal(1234.5)
      result.asInstanceOf[JsNumber].min mustEqual Some(BigDecimal(1234.5))
    }

    "deserialize strings to JsFormatterString" in {
      val result = mapper.readValue( """"one piece of string"""", classOf[JsValue])

      result mustBe an[JsFormattedString]
      result.asInstanceOf[JsFormattedString].value mustEqual "one piece of string"
    }

    "deserialize booleans to JsTrue or JsFalse" in {
      mapper.readValue("true", classOf[JsValue]) mustEqual JsTrue
      mapper.readValue("false", classOf[JsValue]) mustEqual JsFalse
    }

    "deserialize arrays to JsArray" in {
      val result = mapper.readValue( """[1234, 1234.5, true, "one piece of string"]""", classOf[JsValue])

      result mustBe an[JsArray]
      result.asInstanceOf[JsArray].elements mustEqual Seq(
        JsInteger.fixed(1234),
        JsNumber.fixed(1234.5),
        JsTrue,
        JsFormattedString("one piece of string")
      )
      result.asInstanceOf[JsArray].minSize mustEqual Some(4)
    }

    "deserialize objects to JsObject" in {
      val result = mapper.readValue( """{"one": 1234, "two": true, "three": "one piece of string"}""", classOf[JsValue])

      result mustBe an[JsObject]
      result.asInstanceOf[JsObject].required mustEqual Set(
        "one", "two", "three"
      )
      result.asInstanceOf[JsObject].fields mustEqual Map(
        "one" -> JsInteger.fixed(1234),
        "two" -> JsTrue,
        "three" -> JsFormattedString("one piece of string")
      )
    }
  }
}
