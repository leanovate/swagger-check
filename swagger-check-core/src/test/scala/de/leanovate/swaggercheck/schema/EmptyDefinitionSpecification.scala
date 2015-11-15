package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{EmptyDefinition, JsonPath}
import de.leanovate.swaggercheck.shrinkable.{CheckJsNull, CheckJsValue}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}
import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._

object EmptyDefinitionSpecification extends Properties("EmptyDefinition") {
  val swaggerChecks = SwaggerChecks(SwaggerAPI(None, Map.empty, Map.empty))

  property("generate") = forAll(EmptyDefinition.generate(swaggerChecks)) {
    node: CheckJsValue =>
      node == CheckJsNull
  }

  property("verify") = forAll(Gen.oneOf(swaggerChecks.arbitraryObj, swaggerChecks.arbitraryArray, swaggerChecks.arbitraryValue)) {
    node: CheckJsValue =>
      EmptyDefinition.validate(swaggerChecks, JsonPath(), node).isSuccess
  }
}
