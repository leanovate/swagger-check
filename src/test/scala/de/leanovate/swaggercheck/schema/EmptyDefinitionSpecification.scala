package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.SwaggerChecks
import org.scalacheck.{Gen, Properties}
import org.scalacheck.Prop.forAll

object EmptyDefinitionSpecification extends Properties("EmptyDefinition") {
  val swaggerChecks = SwaggerChecks(SwaggerAPI(None, Map.empty, Map.empty))

  property("generate") = forAll(EmptyDefinition.generate(swaggerChecks)) {
    node: JsonNode =>
      node.isNull
  }

  property("verify") = forAll(Gen.oneOf(SchemaObject.arbitraryObj(swaggerChecks), SchemaObject.arbitraryArray(swaggerChecks), SchemaObject.arbitraryValue)) {
    node: JsonNode =>
      EmptyDefinition.verify(swaggerChecks, Nil, node).isSuccess
  }
}
