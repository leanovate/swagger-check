package de.leanovate.swaggercheck.schema.model

import org.scalatest.{MustMatchers, WordSpec}
import org.scalatest.mock.MockitoSugar

class StringDefinitionSpec extends WordSpec with MockitoSugar with MustMatchers {
  "StringDefinition" should {
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
