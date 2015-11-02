package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.leanovate.swaggercheck.SwaggerChecks
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class IntegerDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "IntegerDefinition" should {
    "fail verify for non number nodes" in {
      val mockContext = mock[SwaggerChecks]
      val integerDefinition = IntegerDefinition(None, None, None)

      integerDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.arrayNode()).isSuccess mustBe false
      integerDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.objectNode()).isSuccess mustBe false
      integerDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.booleanNode(false)).isSuccess mustBe false
      integerDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.textNode("")).isSuccess mustBe false
    }

    "fail verify if less than minimum" in {
      val mockContext = mock[SwaggerChecks]
      val integerDefinition = IntegerDefinition(None, Some(10), None)

      integerDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.numberNode(10)).isSuccess mustBe true
      integerDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.numberNode(9)).isSuccess mustBe false
    }

    "fail verify if greater than maximum" in {
      val mockContext = mock[SwaggerChecks]
      val integerDefinition = IntegerDefinition(None, None, Some(20))

      integerDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.numberNode(20)).isSuccess mustBe true
      integerDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.numberNode(21)).isSuccess mustBe false
    }
  }
}
