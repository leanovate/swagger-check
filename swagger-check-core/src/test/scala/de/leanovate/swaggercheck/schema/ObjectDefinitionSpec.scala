package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.shrinkable.{CheckJsArray, CheckJsBoolean, CheckJsInteger, CheckJsString}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class ObjectDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "ObjectDefinition" should {
    "fail verify for non object nodes" in {
      val mockContext = mock[SwaggerChecks]
      val objectDefinition = ObjectDefinition(None, None, None)

      objectDefinition.verify(mockContext, Seq.empty, CheckJsBoolean(false)).isSuccess mustBe false
      objectDefinition.verify(mockContext, Seq.empty, CheckJsArray.empty).isSuccess mustBe false
      objectDefinition.verify(mockContext, Seq.empty, CheckJsInteger(None, None, 0)).isSuccess mustBe false
      objectDefinition.verify(mockContext, Seq.empty, CheckJsString.unformatted("")).isSuccess mustBe false
    }
  }
}
