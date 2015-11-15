package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.JsonPath
import de.leanovate.swaggercheck.shrinkable.{CheckJsNull, CheckJsValue}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}

object EmptyDefinitionSpecification extends Properties("EmptyDefinition") {
  val swaggerChecks = SwaggerChecks(SwaggerAPI(None, Map.empty, Map.empty))

  property("generate") = forAll(EmptyDefinition.generate(swaggerChecks)) {
    node: CheckJsValue =>
      node == CheckJsNull
  }

  property("verify") = forAll(Gen.oneOf(SchemaObject.arbitraryObj(swaggerChecks), SchemaObject.arbitraryArray(swaggerChecks), SchemaObject.arbitraryValue)) {
    node: CheckJsValue =>
      EmptyDefinition.verify(swaggerChecks, JsonPath(), node).isSuccess
  }
}
