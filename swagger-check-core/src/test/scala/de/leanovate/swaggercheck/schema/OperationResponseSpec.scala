package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsString, CheckJsValue}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class OperationResponseSpec extends WordSpec with MustMatchers with MockitoSugar {
  "OperationResponse" should {
    "verify response body" in {
      val swaggerChecks = mock[SwaggerChecks]
      val bodySchema = mock[SchemaObject]
      val response = OperationResponse(Some(bodySchema), Seq.empty)

      when(bodySchema.verify(any(), any(), any())).thenReturn(ValidationResult.success)

      response.verify(swaggerChecks, Map.empty, "{}").isSuccess mustBe true

      verify(bodySchema).verify(swaggerChecks, JsonPath(), CheckJsValue.parse("{}"))
    }

    "verify response headers" in {
      val swaggerChecks = mock[SwaggerChecks]
      val headerSchema = mock[SchemaObject]
      val response = OperationResponse(None, Seq("some header" -> headerSchema))

      when(headerSchema.verify(any(), any(), any())).thenReturn(ValidationResult.success)

      response.verify(swaggerChecks, Map.empty, "{}").isSuccess mustBe true

      verifyZeroInteractions(headerSchema)

      response.verify(swaggerChecks, Map("some header" -> "something"), "{}").isSuccess mustBe true

      verify(headerSchema).verify(swaggerChecks, JsonPath(), CheckJsString.formatted("something"))
    }
  }
}
