package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.JsonPath
import de.leanovate.swaggercheck.shrinkable._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class NumberDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "NumberDefinition" should {
    "fail verify for non number nodes" in {
      val mockContext = mock[SwaggerChecks]
      val numberDefinition = NumberDefinition(None, None, None)

      numberDefinition.verify(mockContext, JsonPath(), CheckJsArray.empty).isSuccess mustBe false
      numberDefinition.verify(mockContext, JsonPath(), CheckJsObject.empty).isSuccess mustBe false
      numberDefinition.verify(mockContext, JsonPath(), CheckJsBoolean(false)).isSuccess mustBe false
      numberDefinition.verify(mockContext, JsonPath(), CheckJsString.unformatted("")).isSuccess mustBe false
    }

    "fail verify if less than minimum" in {
      val mockContext = mock[SwaggerChecks]
      val numberDefinition = NumberDefinition(None, Some(10), None)

      numberDefinition.verify(mockContext, JsonPath(), CheckJsNumber(None, None,10)).isSuccess mustBe true
      numberDefinition.verify(mockContext, JsonPath(), CheckJsNumber(None, None,9)).isSuccess mustBe false
    }

    "fail verify if greater than maximum" in {
      val mockContext = mock[SwaggerChecks]
      val numberDefinition = NumberDefinition(None, None, Some(20))

      numberDefinition.verify(mockContext, JsonPath(), CheckJsNumber(None, None,20)).isSuccess mustBe true
      numberDefinition.verify(mockContext, JsonPath(), CheckJsNumber(None, None,21)).isSuccess mustBe false
    }
  }
}
