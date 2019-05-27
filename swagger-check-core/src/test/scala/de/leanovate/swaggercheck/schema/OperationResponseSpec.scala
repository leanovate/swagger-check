package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{Definition, JsonPath, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsString, CheckJsValue}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar

class OperationResponseSpec extends WordSpec with MustMatchers with MockitoSugar {
  "OperationResponse" should {
    "verify response body" in {
      val swaggerChecks = mock[SwaggerChecks]
      val bodySchema = mock[Definition]
      val response = OperationResponse(Some(bodySchema), Seq.empty)

      when(bodySchema.validate(any(), any(), any())(any())).thenReturn(ValidationResult.success)

      response.verify(swaggerChecks, Map.empty, "{}").isSuccess mustBe true

      verify(bodySchema).validate(swaggerChecks, JsonPath(), CheckJsValue.parse("{}"))(CheckJsValue.Adapter)
    }

    "verify response headers" in {
      val swaggerChecks = mock[SwaggerChecks]
      val headerSchema = mock[Definition]
      val response = OperationResponse(None, Seq("some header" -> headerSchema))

      when(headerSchema.validate(any(), any(), any())(any())).thenReturn(ValidationResult.success)

      response.verify(swaggerChecks, Map.empty, "{}").isSuccess mustBe true

      verifyZeroInteractions(headerSchema)

      response.verify(swaggerChecks, Map("some header" -> "something"), "{}").isSuccess mustBe true

      verify(headerSchema).validate[CheckJsValue](swaggerChecks, JsonPath(), CheckJsString.formatted("something"))(CheckJsValue.Adapter)
    }
  }
}
