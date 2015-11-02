package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.leanovate.swaggercheck.SwaggerChecks
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class NumberDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "NumberDefinition" should {
    "fail verify for non number nodes" in {
      val mockContext = mock[SwaggerChecks]
      val numberDefinition = NumberDefinition(None, None, None)

      numberDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.arrayNode()).isSuccess mustBe false
      numberDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.objectNode()).isSuccess mustBe false
      numberDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.booleanNode(false)).isSuccess mustBe false
      numberDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.textNode("")).isSuccess mustBe false
    }

    "fail verify if less than minimum" in {
      val mockContext = mock[SwaggerChecks]
      val numberDefinition = NumberDefinition(None, Some(10), None)

      numberDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.numberNode(10)).isSuccess mustBe true
      numberDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.numberNode(9)).isSuccess mustBe false
    }

    "fail verify if greater than maximum" in {
      val mockContext = mock[SwaggerChecks]
      val numberDefinition = NumberDefinition(None, None, Some(20))

      numberDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.numberNode(20)).isSuccess mustBe true
      numberDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.numberNode(21)).isSuccess mustBe false
    }
  }
}
