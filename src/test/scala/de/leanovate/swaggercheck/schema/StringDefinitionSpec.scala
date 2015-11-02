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
  }
}
