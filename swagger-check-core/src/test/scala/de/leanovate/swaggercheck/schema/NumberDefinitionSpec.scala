package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{NumberDefinition, JsonPath}
import de.leanovate.swaggercheck.shrinkable._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class NumberDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "NumberDefinition" should {
    "fail verify for non number nodes" in {
      val mockContext = mock[SwaggerChecks]
      val numberDefinition = NumberDefinition(None, None, None)

      numberDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsArray.empty).isSuccess mustBe false
      numberDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsObject.empty).isSuccess mustBe false
      numberDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsBoolean(false)).isSuccess mustBe false
      numberDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsString.unformatted("")).isSuccess mustBe false
    }

    "fail verify if less than minimum" in {
      val mockContext = mock[SwaggerChecks]
      val numberDefinition = NumberDefinition(None, Some(10), None)

      numberDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsNumber(None, None,10)).isSuccess mustBe true
      numberDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsNumber(None, None,9)).isSuccess mustBe false
    }

    "fail verify if greater than maximum" in {
      val mockContext = mock[SwaggerChecks]
      val numberDefinition = NumberDefinition(None, None, Some(20))

      numberDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsNumber(None, None,20)).isSuccess mustBe true
      numberDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsNumber(None, None,21)).isSuccess mustBe false
    }
  }
}
