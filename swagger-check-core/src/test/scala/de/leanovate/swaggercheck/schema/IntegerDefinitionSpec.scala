package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{IntegerDefinition, JsonPath}
import de.leanovate.swaggercheck.shrinkable._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class IntegerDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "IntegerDefinition" should {
    "fail verify for non number nodes" in {
      val mockContext = mock[SwaggerChecks]
      val integerDefinition = IntegerDefinition(None, None, None)

      integerDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsArray.empty).isSuccess mustBe false
      integerDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsObject.empty).isSuccess mustBe false
      integerDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsBoolean(false)).isSuccess mustBe false
      integerDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsString.unformatted("")).isSuccess mustBe false
      integerDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsNumber(None, None, 10.25)).isSuccess mustBe false
    }

    "fail verify if less than minimum" in {
      val mockContext = mock[SwaggerChecks]
      val integerDefinition = IntegerDefinition(None, Some(10), None)

      integerDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsInteger(None, None, 10)).isSuccess mustBe true
      integerDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsInteger(None, None,9)).isSuccess mustBe false
    }

    "fail verify if greater than maximum" in {
      val mockContext = mock[SwaggerChecks]
      val integerDefinition = IntegerDefinition(None, None, Some(20))

      integerDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsInteger(None, None,20)).isSuccess mustBe true
      integerDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsInteger(None, None,21)).isSuccess mustBe false
    }
  }
}
