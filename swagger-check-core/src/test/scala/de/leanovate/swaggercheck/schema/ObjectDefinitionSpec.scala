package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.JsonPath
import de.leanovate.swaggercheck.shrinkable.{CheckJsArray, CheckJsBoolean, CheckJsInteger, CheckJsString}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class ObjectDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "ObjectDefinition" should {
    "fail verify for non object nodes" in {
      val mockContext = mock[SwaggerChecks]
      val objectDefinition = ObjectDefinition(None, None, None)

      objectDefinition.verify(mockContext, JsonPath(), CheckJsBoolean(false)).isSuccess mustBe false
      objectDefinition.verify(mockContext, JsonPath(), CheckJsArray.empty).isSuccess mustBe false
      objectDefinition.verify(mockContext, JsonPath(), CheckJsInteger(None, None, 0)).isSuccess mustBe false
      objectDefinition.verify(mockContext, JsonPath(), CheckJsString.unformatted("")).isSuccess mustBe false
    }
  }
}
