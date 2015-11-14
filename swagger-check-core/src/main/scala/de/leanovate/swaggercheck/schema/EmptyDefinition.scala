package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.shrinkable.{CheckJsNull, CheckJsValue}
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

object EmptyDefinition extends SchemaObject {

  override def generate(context: SwaggerChecks): Gen[CheckJsValue] = Gen.const(CheckJsNull)

  override def verify(context: SwaggerChecks, path: Seq[String], node: CheckJsValue): VerifyResult = VerifyResult.success
}
