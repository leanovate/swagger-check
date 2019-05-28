package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.model.formats.ValueFormat
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class IntegerDefinitionSpec extends WordSpec with MockitoSugar with MustMatchers {
  "IntegerDefinition" should {
    "accept any integer if no format or range is defined" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(integer = Some(BigInt(Long.MaxValue) + 12345))
      val schema = mock[Schema]

      val definition = IntegerDefinition(None, None, None)

      definition.validate(schema, path, node) mustBe ValidationSuccess
    }

    "accept values that match the defined format" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(integer = Some(BigInt(12345)))
      val schema = mock[Schema]
      val format = mock[ValueFormat[BigInt]]

      when(schema.findIntegerFormat("theformat")).thenReturn(Some(format))
      when(format.validate(path, BigInt(12345))).thenReturn(ValidationResult.success)

      val definition = IntegerDefinition(Some("theformat"), None, None)

      definition.validate(schema, path, node) mustBe ValidationSuccess

      verify(schema).findIntegerFormat("theformat")
      verify(format).validate(path, BigInt(12345))
    }

    "fail validation if value is less than minimum" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(integer = Some(BigInt(12345)))
      val schema = mock[Schema]

      val definition = IntegerDefinition(None, Some(BigInt(123456)), None)

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("has to be greater than 123456 in path jsonpath")
    }

    "fail validation if value is greater than maximum" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(integer = Some(BigInt(123456)))
      val schema = mock[Schema]

      val definition = IntegerDefinition(None, None, Some(BigInt(12345)))

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("has to be less than 12345 in path jsonpath")
    }

    "fail validation on everything that is not an integer" in {
      val path = JsonPath("jsonpath")
      val node = TestNode()
      val schema = mock[Schema]

      val definition = IntegerDefinition(None, None, None)

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("should be an integer in path jsonpath")
    }
  }
}
