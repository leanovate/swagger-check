package de.leanovate.swaggercheck.schema.model

import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class EmptyDefinitionSpec extends WordSpec with MockitoSugar with MustMatchers {
  "EmptyDefinition" should {
    "validate anything" in {
      val path = JsonPath("jsonpath")
      val node = TestNode()
      val schema = mock[Schema]

      val definition = EmptyDefinition

      definition.validate(schema, path, node) mustBe ValidationSuccess(node)
    }
  }
}
