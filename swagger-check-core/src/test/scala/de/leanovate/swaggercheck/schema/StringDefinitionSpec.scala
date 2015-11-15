package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{StringDefinition, JsonPath}
import de.leanovate.swaggercheck.shrinkable._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class StringDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "StringDefinition" should {
    "fail verify for non text nodes" in {
      val mockContext = mock[SwaggerChecks]
      val stringDefinition = StringDefinition(None, None, None, None, None)

      stringDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsArray.empty).isSuccess mustBe false
      stringDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsObject.empty).isSuccess mustBe false
      stringDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsBoolean(false)).isSuccess mustBe false
      stringDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsInteger(None, None, 0)).isSuccess mustBe false
    }

    "fail verify if less then minLength" in {
      val mockContext = mock[SwaggerChecks]
      val stringDefinition = StringDefinition(None, Some(3), None, None, None)

      stringDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsString.unformatted("123")).isSuccess mustBe true
      stringDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsString.unformatted("12")).isSuccess mustBe false
    }

    "fail verify if more then maxLength" in {
      val mockContext = mock[SwaggerChecks]
      val stringDefinition = StringDefinition(None, None, Some(5), None, None)

      stringDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsString.unformatted("12345")).isSuccess mustBe true
      stringDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsString.unformatted("123456")).isSuccess mustBe false
    }

    "fail for values not in enum" in {
      val mockContext = mock[SwaggerChecks]
      val stringDefinition = StringDefinition(None, None, None, None, Some("E1" :: "E2" :: "E3" :: Nil))

      stringDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsString.unformatted("E2")).isSuccess mustBe true
      stringDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsString.unformatted("E4")).isSuccess mustBe false
    }
  }
}
