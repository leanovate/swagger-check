package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

object EmptyDefinition extends SchemaObject {

  import SchemaObject._

  override def generate(context: SwaggerChecks): Gen[JsonNode] = Gen.const(nodeFactory.nullNode())

  override def verify(context: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = VerifyResult.success
}
