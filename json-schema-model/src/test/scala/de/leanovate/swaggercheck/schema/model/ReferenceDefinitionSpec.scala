package de.leanovate.swaggercheck.schema.model

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import org.mockito.Mockito._

class ReferenceDefinitionSpec extends WordSpec with MockitoSugar with MustMatchers {
  "ReferenceDefinition" should {
    "delegate validation to referenced definition" in {
      val path = JsonPath("jsonpath")
      val node = TestNode()
      val schema = mock[Schema]
      val referencedDefinition = mock[Definition]

      when(schema.findByRef("reference")).thenReturn(Some(referencedDefinition))
      when(referencedDefinition.validate(schema, path, node)).thenReturn(ValidationResult.error("error1"))

      val definition = ReferenceDefinition("reference")

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head mustBe "error1"
    }

    "fail validation if referenced definition does not exists" in {
      val path = JsonPath("jsonpath")
      val node = TestNode()
      val schema = mock[Schema]

      when(schema.findByRef("reference")).thenReturn(None)

      val definition = ReferenceDefinition("reference")

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head mustBe "Referenced definition does not exists: reference"
    }
  }
}
