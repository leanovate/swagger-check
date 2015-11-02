package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.leanovate.swaggercheck.SwaggerChecks
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class BooleanDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "BooleanDefinition" should {
    "fail verify for non boolean nodes" in {
      val mockContext = mock[SwaggerChecks]
      BooleanDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.arrayNode()).isSuccess mustBe false
      BooleanDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.objectNode()).isSuccess mustBe false
      BooleanDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.numberNode(0)).isSuccess mustBe false
      BooleanDefinition.verify(mockContext, Seq.empty, JsonNodeFactory.instance.textNode("")).isSuccess mustBe false
    }
  }
}
