package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.leanovate.swaggercheck.SwaggerChecks
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class ObjectDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "ObjectDefinition" should {
    "fail verify for non object nodes" in {
      val mockContext = mock[SwaggerChecks]
      val objectDefinition = ObjectDefinition(None, None, None)

      objectDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.booleanNode(false)).isSuccess mustBe false
      objectDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.arrayNode()).isSuccess mustBe false
      objectDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.numberNode(0)).isSuccess mustBe false
      objectDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.textNode("")).isSuccess mustBe false
    }
  }
}
