package de.leanovate.swaggercheck.schema.model

import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class ArrayDefinitionSpec extends WordSpec with MockitoSugar with MustMatchers {
  "ArrayDefinition" should {
    "accept any array if no item definition is set" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(array = Some(Seq(TestNode(), TestNode())))
      val schema = mock[Schema]

      val definition = ArrayDefinition(None, None, None)

      val result = definition.validate(schema, path, node)

      result mustBe ValidationSuccess
    }

    "succeed if item definition succeeds on all elements" in {
      val path = JsonPath("jsonpath")
      val item1 = TestNode()
      val item2 = TestNode()
      val node = TestNode(array = Some(Seq(item1, item2)))
      val schema = mock[Schema]
      val itemDefinition = mock[Definition]

      when(itemDefinition.validate(schema, path.index(0), item1)).thenReturn(ValidationSuccess)
      when(itemDefinition.validate(schema, path.index(1), item2)).thenReturn(ValidationSuccess)

      val definition = ArrayDefinition(None, None, Some(itemDefinition))

      definition.validate(schema, path, node) mustBe ValidationSuccess

      verify(itemDefinition).validate(schema, path.index(0), item1)
      verify(itemDefinition).validate(schema, path.index(1), item2)
    }

    "fail if item definition fails on one element" in {
      val path = JsonPath("jsonpath")
      val item1 = TestNode()
      val item2 = TestNode()
      val node = TestNode(array = Some(Seq(item1, item2)))
      val schema = mock[Schema]
      val itemDefinition = mock[Definition]

      when(itemDefinition.validate(schema, path.index(0), item1)).thenReturn(ValidationResult.error("error"))
      when(itemDefinition.validate(schema, path.index(1), item2)).thenReturn(ValidationSuccess)

      val definition = ArrayDefinition(None, None, Some(itemDefinition))

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head mustBe "error"
    }

    "fail if array has less then minItems" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(array = Some(Seq(TestNode(), TestNode())))
      val schema = mock[Schema]

      val definition = ArrayDefinition(Some(3), None, None)

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("should have at least 3 items in path jsonpath")
    }

    "fail if array has more then maxItems" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(array = Some(Seq(TestNode(), TestNode())))
      val schema = mock[Schema]

      val definition = ArrayDefinition(None, Some(1), None)

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("should have at least 1 items in path jsonpath")
    }

    "fail validation on everything that is not an array" in {
      val path = JsonPath("jsonpath")
      val node = TestNode()
      val schema = mock[Schema]

      val definition = ArrayDefinition(None, None, None)

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("should be an array in path jsonpath")
    }
  }
}
