package de.leanovate.swaggercheck.schema.model

import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class ObjectDefinitionSpec extends WordSpec with MockitoSugar with MustMatchers {
  "ObjectDefinition" should {
    "accept any object if no property definition is set" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(obj = Some(Map("field1" -> TestNode(), "field2" -> TestNode())))
      val schema = mock[Schema]

      val definition = ObjectDefinition(None, None, None)

      definition.validate(schema, path, node) mustBe ValidationSuccess
    }

    "succeed if non-required fields are missing" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(obj = Some(Map("field1" -> TestNode(), "field2" -> TestNode())))
      val schema = mock[Schema]
      val field3Definition = mock[Definition]
      val field4Definition = mock[Definition]

      val definition = ObjectDefinition(None, Some(Map("field3" -> field3Definition, "field4" -> field4Definition)), None)

      definition.validate(schema, path, node) mustBe ValidationSuccess

      verifyZeroInteractions(field3Definition, field4Definition)
    }

    "fail if a required fields are missing" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(obj = Some(Map("field1" -> TestNode(), "field2" -> TestNode())))
      val schema = mock[Schema]
      val field3Definition = mock[Definition]
      val field4Definition = mock[Definition]

      when(field3Definition.validate(schema, path.field("field3"), TestNode(isNull = true))).thenReturn(ValidationResult.error("error1"))
      when(field4Definition.validate(schema, path.field("field4"), TestNode(isNull = true))).thenReturn(ValidationResult.error("error2"))

      val definition = ObjectDefinition(Some(Set("field3", "field4")), Some(Map("field3" -> field3Definition, "field4" -> field4Definition)), None)

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result mustBe Seq("error1", "error2")
    }

    "succeed if all fields succeed" in {
      val path = JsonPath("jsonpath")
      val field2 = TestNode()
      val field3 = TestNode()
      val field4 = TestNode()
      val node = TestNode(obj = Some(Map("field1" -> TestNode(), "field2" -> field2, "field3" -> field3, "field4" -> field4)))
      val schema = mock[Schema]
      val field2Definition = mock[Definition]
      val field3Definition = mock[Definition]
      val field4Definition = mock[Definition]

      when(field2Definition.validate(schema, path.field("field2"), field2)).thenReturn(ValidationResult.success)
      when(field3Definition.validate(schema, path.field("field3"), field3)).thenReturn(ValidationResult.success)
      when(field4Definition.validate(schema, path.field("field4"), field4)).thenReturn(ValidationResult.success)

      val definition = ObjectDefinition(Some(Set("field3", "field4")),
        Some(Map("field2" -> field2Definition, "field3" -> field3Definition, "field4" -> field4Definition)), None)

      definition.validate(schema, path, node) mustBe ValidationSuccess

      verify(field2Definition).validate(schema, path.field("field2"), field2)
      verify(field3Definition).validate(schema, path.field("field3"), field3)
      verify(field4Definition).validate(schema, path.field("field4"), field4)
    }

    "fail if additional fields do not match definition" in {
      val path = JsonPath("jsonpath")
      val field1 = TestNode()
      val field2 = TestNode()
      val field3 = TestNode()
      val field4 = TestNode()
      val node = TestNode(obj = Some(Map("field1" -> field1, "field2" -> field2, "field3" -> field3, "field4" -> field4)))
      val schema = mock[Schema]
      val additionalDefinition = mock[Definition]
      val field2Definition = mock[Definition]
      val field3Definition = mock[Definition]
      val field4Definition = mock[Definition]

      when(additionalDefinition.validate(schema, path.field("field1"), field1)).thenReturn(ValidationResult.error("error"))
      when(field2Definition.validate(schema, path.field("field2"), field2)).thenReturn(ValidationResult.success)
      when(field3Definition.validate(schema, path.field("field3"), field3)).thenReturn(ValidationResult.success)
      when(field4Definition.validate(schema, path.field("field4"), field4)).thenReturn(ValidationResult.success)

      val definition = ObjectDefinition(Some(Set("field3", "field4")),
        Some(Map("field2" -> field2Definition, "field3" -> field3Definition, "field4" -> field4Definition)),
        Some(additionalDefinition))

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result mustBe Seq("error")
    }

    "succeed if additional fields match definition" in {
      val path = JsonPath("jsonpath")
      val field1 = TestNode()
      val field2 = TestNode()
      val field3 = TestNode()
      val field4 = TestNode()
      val node = TestNode(obj = Some(Map("field1" -> field1, "field2" -> field2, "field3" -> field3, "field4" -> field4)))
      val schema = mock[Schema]
      val additionalDefinition = mock[Definition]
      val field2Definition = mock[Definition]
      val field3Definition = mock[Definition]
      val field4Definition = mock[Definition]

      when(additionalDefinition.validate(schema, path.field("field1"), field1)).thenReturn(ValidationResult.success)
      when(field2Definition.validate(schema, path.field("field2"), field2)).thenReturn(ValidationResult.success)
      when(field3Definition.validate(schema, path.field("field3"), field3)).thenReturn(ValidationResult.success)
      when(field4Definition.validate(schema, path.field("field4"), field4)).thenReturn(ValidationResult.success)

      val definition = ObjectDefinition(Some(Set("field3", "field4")),
        Some(Map("field2" -> field2Definition, "field3" -> field3Definition, "field4" -> field4Definition)),
        Some(additionalDefinition))

      definition.validate(schema, path, node) mustBe ValidationSuccess

      verify(additionalDefinition).validate(schema, path.field("field1"), field1)
      verify(field2Definition).validate(schema, path.field("field2"), field2)
      verify(field3Definition).validate(schema, path.field("field3"), field3)
      verify(field4Definition).validate(schema, path.field("field4"), field4)
    }

    "fail validation on everything that is not an integer" in {
      val path = JsonPath("jsonpath")
      val node = TestNode()
      val schema = mock[Schema]

      val definition = ObjectDefinition(None, None, None)

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("should be an object in path jsonpath")
    }
  }
}
