package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.leanovate.swaggercheck.SwaggerChecks
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class StringDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "StringDefinition" should {
    "fail verify for non text nodes" in {
      val mockContext = mock[SwaggerChecks]
      val stringDefinition = StringDefinition(None, None, None, None, None)

      stringDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.arrayNode()).isSuccess mustBe false
      stringDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.objectNode()).isSuccess mustBe false
      stringDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.booleanNode(false)).isSuccess mustBe false
      stringDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.numberNode(0)).isSuccess mustBe false
    }

    "fail verify if less then minLength" in {
      val mockContext = mock[SwaggerChecks]
      val stringDefinition = StringDefinition(None, Some(3), None, None, None)

      stringDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.textNode("123")).isSuccess mustBe true
      stringDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.textNode("12")).isSuccess mustBe false
    }

    "fail verify if more then maxLength" in {
      val mockContext = mock[SwaggerChecks]
      val stringDefinition = StringDefinition(None, None, Some(5), None, None)

      stringDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.textNode("12345")).isSuccess mustBe true
      stringDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.textNode("123456")).isSuccess mustBe false
    }

    "fail for values not in enum" in {
      val mockContext = mock[SwaggerChecks]
      val stringDefinition = StringDefinition(None, None, None, None, Some("E1" :: "E2" :: "E3" :: Nil))

      stringDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.textNode("E2")).isSuccess mustBe true
      stringDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.textNode("E4")).isSuccess mustBe false

    }
  }
}
