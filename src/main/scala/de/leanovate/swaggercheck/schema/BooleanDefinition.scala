package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

object BooleanDefinition extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] =
    Gen.oneOf(nodeFactory.booleanNode(true), nodeFactory.booleanNode(false))

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    if (node.isBoolean) {
      VerifyResult.success
    } else {
      VerifyResult.error(s"${node.getNodeType} should be a boolean: ${path.mkString(".")}")
    }
  }
}
