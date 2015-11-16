package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.model.formats.ValueFormat
import org.mockito.Mockito._
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatest.mock.MockitoSugar

class StringDefinitionSpec extends WordSpec with MockitoSugar with MustMatchers {
  "StringDefinition" should {
    "accept any string if no format or range is defined" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(string = Some("totally unimportant"))
      val schema = mock[Schema]

      val definition = StringDefinition(None, None, None, None, None)

      definition.validate(schema, path, node) mustBe ValidateSuccess
    }

    "accept values that match the defined format" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(string = Some("some string"))
      val schema = mock[Schema]
      val format = mock[ValueFormat[String]]

      when(schema.findStringFormat("theformat")).thenReturn(Some(format))
      when(format.validate(path, "some string")).thenReturn(ValidationResult.success)

      val definition = StringDefinition(Some("theformat"), None, None, None, None)

      definition.validate(schema, path, node) mustBe ValidateSuccess

      verify(schema).findStringFormat("theformat")
      verify(format).validate(path, "some string")
    }

    "fail validation if value is shorter less than minLength" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(string = Some("1234567890"))
      val schema = mock[Schema]

      val definition = StringDefinition(None, Some(11), None, None, None)

      val ValidationError(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("has to be at least 11 chars long in path jsonpath")
    }

    "fail validation if value is longer less than maxLength" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(string = Some("1234567890"))
      val schema = mock[Schema]

      val definition = StringDefinition(None, None, Some(9), None, None)

      val ValidationError(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("has to be at most 9 chars long in path jsonpath")
    }

    "accept string that match pattern" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(string = Some("298de822-4f5a-40ca-99fd-58b105408f9e"))
      val schema = mock[Schema]

      val definition = StringDefinition(None, None, None, Some("[0-9a-f]{8}(\\-[0-9a-f]{4}){3}\\-[0-9a-f]{12}"), None)

      definition.validate(schema, path, node) mustBe ValidateSuccess
    }

    "fail if string does not match pattern" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(string = Some("totally unimportant"))
      val schema = mock[Schema]

      val definition = StringDefinition(None, None, None, Some("[0-9a-f]{8}(\\-[0-9a-f]{4}){3}\\-[0-9a-f]{12}"), None)

      val ValidationError(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("has match '[0-9a-f]{8}(\\-[0-9a-f]{4}){3}\\-[0-9a-f]{12}' in path jsonpath")
    }

    "accept strings contained in enum" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(string = Some("enum2"))
      val schema = mock[Schema]

      val definition = StringDefinition(None, None, None, None, Some("enum1" :: "enum2":: "enum3" :: Nil))

      definition.validate(schema, path, node) mustBe ValidateSuccess
    }

    "fail if string is not contained in enum" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(string = Some("totally unimportant"))
      val schema = mock[Schema]

      val definition = StringDefinition(None, None, None, None, Some("enum1" :: "enum2":: "enum3" :: Nil))

      val ValidationError(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("has to be one of enum1, enum2, enum3 in path jsonpath")
    }

    "fail validation on everything that is not a string" in {
      val path = JsonPath("jsonpath")
      val node = TestNode()
      val schema = mock[Schema]

      val definition = StringDefinition(None, None, None, None, None)

      val ValidationError(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("should be a string in path jsonpath")
    }
  }
}
