package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{BooleanDefinition, JsonPath}
import de.leanovate.swaggercheck.shrinkable._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class BooleanDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "BooleanDefinition" should {
    "fail verify for non boolean nodes" in {
      val mockContext = mock[SwaggerChecks]
      BooleanDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsArray.empty).isSuccess mustBe false
      BooleanDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsObject.empty).isSuccess mustBe false
      BooleanDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsInteger(None, None, 0)).isSuccess mustBe false
      BooleanDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsString.unformatted("")).isSuccess mustBe false
    }
  }
}
