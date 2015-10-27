package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.{VerifyResult, SwaggerChecks}
import org.scalacheck.Gen

@JsonTypeName("boolean")
case class BooleanDefinition(

                              ) extends SchemaObject {

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
