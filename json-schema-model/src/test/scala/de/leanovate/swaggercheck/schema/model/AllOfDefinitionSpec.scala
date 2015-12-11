package de.leanovate.swaggercheck.schema.model

import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import org.mockito.Mockito._

class AllOfDefinitionSpec extends WordSpec with MockitoSugar with MustMatchers {
  "AllOfDefinition" should {
    "succeed validation if all children succeed" in {
      val definition1 = mock[Definition]
      val definition2 = mock[Definition]
      val definition3 = mock[Definition]
      val schema = mock[Schema]
      val path = JsonPath("path")
      val node = TestNode()

      when(definition1.validate(schema, path, node)).thenReturn(ValidationResult.success(node))
      when(definition2.validate(schema, path, node)).thenReturn(ValidationResult.success(node))
      when(definition3.validate(schema, path, node)).thenReturn(ValidationResult.success(node))

      val definition = AllOfDefinition(Seq(definition1, definition2, definition3))

      definition.validate(schema, path, node) mustBe ValidationSuccess(node)

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

      when(definition1.validate(schema, path, node)).thenReturn(ValidationResult.success(node))
      when(definition2.validate(schema, path, node)).thenReturn(ValidationResult.error[TestNode]("error"))
      when(definition3.validate(schema, path, node)).thenReturn(ValidationResult.success(node))

      val definition = AllOfDefinition(Seq(definition1, definition2, definition3))

      val result = definition.validate(schema, path, node)

      result mustBe ValidationResult.error("error")
    }
  }
}
