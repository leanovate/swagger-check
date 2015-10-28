package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

import scala.collection.JavaConversions._

case class AllOfDefinition(schemas: Seq[SchemaObject]) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] =
    Gen.sequence(schemas.map(_.generate(ctx))).map(_.foldLeft(nodeFactory.objectNode()) {
      case (result, other: ObjectNode) =>
        other.fields().foldLeft(result) {
          (result, entry) =>
            result.set(entry.getKey, entry.getValue)
            result
        }
      case (result, _) => result
    })

  override def verify(context: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult =
    schemas.foldLeft(VerifyResult.success) {
      (result, schema) =>
        result.combine(schema.verify(context, path, node))
    }
}
