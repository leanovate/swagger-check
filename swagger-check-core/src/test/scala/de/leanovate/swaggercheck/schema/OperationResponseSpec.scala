package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{Definition, JsonPath, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsObject, CheckJsString, CheckJsValue}
import org.mockito.Matchers._
import org.mockito.Matchers.{eq => eql}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class OperationResponseSpec extends WordSpec with MustMatchers with MockitoSugar {
  "OperationResponse" should {
    "verify response body" in {
      val swaggerChecks = mock[SwaggerChecks]
      val bodySchema = mock[Definition]
      val response = OperationResponse(Some(bodySchema), Seq.empty)

      when(bodySchema.validate(any(), any(), any(classOf[CheckJsValue]))(eql(CheckJsValue.adapter))).thenReturn(ValidationResult.success(CheckJsObject.empty))

      response.verify(swaggerChecks, Map.empty, "{}").isSuccess mustBe true

      verify(bodySchema).validate(swaggerChecks, JsonPath(), CheckJsValue.parse("{}"))(CheckJsValue.adapter)
    }

    "verify response headers" in {
      val swaggerChecks = mock[SwaggerChecks]
      val headerSchema = mock[Definition]
      val response = OperationResponse(None, Seq("some header" -> headerSchema))

      when(headerSchema.validate(any(), any(), any(classOf[CheckJsValue]))(eql(CheckJsValue.adapter))).thenReturn(ValidationResult.success(CheckJsObject.empty))

      response.verify(swaggerChecks, Map.empty, "{}").isSuccess mustBe true

      verifyZeroInteractions(headerSchema)

      response.verify(swaggerChecks, Map("some header" -> "something"), "{}").isSuccess mustBe true

      verify(headerSchema).validate[CheckJsValue](swaggerChecks, JsonPath(), CheckJsString.formatted("something"))(CheckJsValue.adapter)
    }
  }
}
