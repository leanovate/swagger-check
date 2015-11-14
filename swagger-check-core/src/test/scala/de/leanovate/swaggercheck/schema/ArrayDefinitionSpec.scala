package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.model.{CheckJsString, CheckJsInteger, CheckJsObject, CheckJsBoolean}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class ArrayDefinitionSpec extends WordSpec with MustMatchers with MockitoSugar {
  "ArrayDefinition" should {
    "fail verify for non array nodes" in {
      val mockContext = mock[SwaggerChecks]
      val arrayDefinition = ArrayDefinition(None, None, None)

      arrayDefinition.verify(mockContext, Seq.empty, CheckJsBoolean(false)).isSuccess mustBe false
      arrayDefinition.verify(mockContext, Seq.empty, CheckJsObject.empty).isSuccess mustBe false
      arrayDefinition.verify(mockContext, Seq.empty, CheckJsInteger(None, None, 0)).isSuccess mustBe false
      arrayDefinition.verify(mockContext, Seq.empty, CheckJsString.unformatted("")).isSuccess mustBe false
    }
  }

}
