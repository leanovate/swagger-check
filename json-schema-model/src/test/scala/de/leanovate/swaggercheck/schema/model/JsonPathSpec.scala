package de.leanovate.swaggercheck.schema.model

import org.scalatest.{MustMatchers, WordSpec}

class JsonPathSpec extends WordSpec with MustMatchers {
  "JsonPath" should {
    "has toString" in {
      val jsonPath = JsonPath("the.path")

      jsonPath.toString mustBe "the.path"
    }

    "concat fields names and indexes" in {
      val root = JsonPath()
      val sub1 = root.field("field1")
      val sub2 = sub1.field("field2")
      val sub3 = sub2.index(10)
      val sub4 = sub3.field("field3")

      root.toString mustBe ""
      sub1.toString mustBe "field1"
      sub2.toString mustBe "field1.field2"
      sub3.toString mustBe "field1.field2[10]"
      sub4.toString mustBe "field1.field2[10].field3"
    }
  }
}
