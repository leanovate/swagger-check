package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.shrinkable.{CheckJsArray, CheckJsInteger, CheckJsObject, CheckJsString}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class BooleanDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "BooleanDefinition" should {
    "fail verify for non boolean nodes" in {
      val mockContext = mock[SwaggerChecks]
      BooleanDefinition.verify(mockContext, Seq.empty, CheckJsArray.empty).isSuccess mustBe false
      BooleanDefinition.verify(mockContext, Seq.empty, CheckJsObject.empty).isSuccess mustBe false
      BooleanDefinition.verify(mockContext, Seq.empty, CheckJsInteger(None, None, 0)).isSuccess mustBe false
      BooleanDefinition.verify(mockContext, Seq.empty, CheckJsString.unformatted("")).isSuccess mustBe false
    }
  }
}
