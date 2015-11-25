package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{BooleanDefinition, StringDefinition, ObjectDefinition, JsonPath}
import de.leanovate.swaggercheck.shrinkable._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class ObjectDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "ObjectDefinition" should {
    "fail verify for non object nodes" in {
      val mockContext = mock[SwaggerChecks]
      val objectDefinition = ObjectDefinition(None, None, Left(true))

      objectDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsBoolean(false)).isSuccess mustBe false
      objectDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsArray.empty).isSuccess mustBe false
      objectDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsInteger(None, None, 0)).isSuccess mustBe false
      objectDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsString.unformatted("")).isSuccess mustBe false
    }

    "fail if additional properties are not allowed" in {
      val mockContext = mock[SwaggerChecks]
      val objectDefinition = ObjectDefinition(None, Some(Map(
        "field1" -> BooleanDefinition
      )), Left(false))

      objectDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsObject.fixed(Seq(
        "field1" -> CheckJsBoolean(true)
      ))).isSuccess mustBe true
      objectDefinition.validate[CheckJsValue](mockContext, JsonPath(), CheckJsObject.fixed(Seq(
        "field1" -> CheckJsBoolean(true),
        "field2" -> CheckJsBoolean(false)
      ))).isSuccess mustBe false
    }
  }
}
