package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.model.formats.ValueFormat
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class NumberDefinitionSpec extends WordSpec with MockitoSugar with MustMatchers {
  "NumberDefinition" should {
    "accept any integer if no format or range is defined" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(number = Some(BigDecimal(Long.MaxValue) + 12345))
      val schema = mock[Schema]

      val definition = NumberDefinition(None, None, None)

      definition.validate(schema, path, node) mustBe ValidateSuccess
    }

    "accept values that match the defined format" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(number = Some(BigDecimal(12345.67)))
      val schema = mock[Schema]
      val format = mock[ValueFormat[BigDecimal]]

      when(schema.findNumberFormat("theformat")).thenReturn(Some(format))
      when(format.validate(path, BigDecimal(12345.67))).thenReturn(ValidationResult.success)

      val definition = NumberDefinition(Some("theformat"), None, None)

      definition.validate(schema, path, node) mustBe ValidateSuccess

      verify(schema).findNumberFormat("theformat")
      verify(format).validate(path, BigDecimal(12345.67))
    }

    "fail validation if value is less than minimum" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(number = Some(BigDecimal(12345.6)))
      val schema = mock[Schema]

      val definition = NumberDefinition(None, Some(BigDecimal(123456.7)), None)

      val ValidationError(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("has to be greater than 123456.7 in path jsonpath")
    }

    "fail validation if value is greater than maximum" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(number = Some(BigDecimal(123456.7)))
      val schema = mock[Schema]

      val definition = NumberDefinition(None, None, Some(BigDecimal(12345.6)))

      val ValidationError(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("has to be less than 12345.6 in path jsonpath")
    }

    "fail validation on everything that is not an integer" in {
      val path = JsonPath("jsonpath")
      val node = TestNode()
      val schema = mock[Schema]

      val definition = NumberDefinition(None, None, None)

      val ValidationError(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("should be a number in path jsonpath")
    }
  }
}
