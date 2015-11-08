package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.model.{CheckJsNull, CheckJsValue}
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

object EmptyDefinition extends SchemaObject {

  override def generate(context: SwaggerChecks): Gen[CheckJsValue] = Gen.const(CheckJsNull)

  override def verify(context: SwaggerChecks, path: Seq[String], node: CheckJsValue): VerifyResult = VerifyResult.success
}
