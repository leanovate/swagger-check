package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsNull, CheckJsValue}
import org.scalacheck.Gen

object EmptyDefinition extends SchemaObject {

  override def generate(context: SwaggerChecks): Gen[CheckJsValue] = Gen.const(CheckJsNull)

  override def verify(context: SwaggerChecks, path: JsonPath, node: CheckJsValue): ValidationResult = ValidationResult.success
}
