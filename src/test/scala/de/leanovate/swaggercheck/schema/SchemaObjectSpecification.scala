package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.SwaggerChecks
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object SchemaObjectSpecification extends Properties("SchemaObject") {
  val swaggerChecks = SwaggerChecks(SwaggerAPI(None, Map.empty, Map.empty))

  property("arbitraryObject") = forAll(SchemaObject.arbitraryObj(swaggerChecks)) {
    node: JsonNode =>
      node.isObject
  }

  property("arbitraryArray") = forAll(SchemaObject.arbitraryArray(swaggerChecks)) {
    node: JsonNode =>
      node.isArray
  }

  property("arbitraryValue") = forAll(SchemaObject.arbitraryValue) {
    node: JsonNode =>
      node.isTextual || node.isNumber || node.isBoolean
  }
}
