package de.leanovate.swaggercheck.schema.model

import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class OneOfDefinitionSpec extends WordSpec with MockitoSugar with MustMatchers {
  "OneOfDefinition" should {
    "succeed validation if one child succeed" in {
      val definition1 = mock[Definition]
      val definition2 = mock[Definition]
      val definition3 = mock[Definition]
      val schema = mock[Schema]
      val path = JsonPath("path")
      val node = TestNode()

      when(definition1.validate(schema, path, node)).thenReturn(ValidationResult.error("error1"))
      when(definition2.validate(schema, path, node)).thenReturn(ValidationResult.success)
      when(definition3.validate(schema, path, node)).thenReturn(ValidationResult.error("error2"))

      val definition = OneOfDefinition(Seq(definition1, definition2, definition3))

      definition.validate(schema, path, node) mustBe ValidationSuccess

      verify(definition1).validate(schema, path, node)
      verify(definition2).validate(schema, path, node)
      verify(definition3).validate(schema, path, node)
    }

    "fail validation if one child fails" in {
      val definition1 = mock[Definition]
      val definition2 = mock[Definition]
      val definition3 = mock[Definition]
      val schema = mock[Schema]
      val path = JsonPath("path")
      val node = TestNode()

      when(definition1.validate(schema, path, node)).thenReturn(ValidationResult.error("error1"))
      when(definition2.validate(schema, path, node)).thenReturn(ValidationResult.error("error2"))
      when(definition3.validate(schema, path, node)).thenReturn(ValidationResult.error("error3"))

      val definition = OneOfDefinition(Seq(definition1, definition2, definition3))

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result mustBe Seq("error1", "error2", "error3")
    }
  }
}
