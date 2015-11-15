package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsObject, CheckJsValue}
import de.leanovate.swaggercheck.{SwaggerChecks}
import org.scalacheck.Gen

import scala.collection.JavaConversions._

case class AllOfDefinition(schemas: Seq[SchemaObject]) extends SchemaObject {

  override def generate(ctx: SwaggerChecks): Gen[CheckJsValue] =
    Gen.sequence(schemas.map(_.generate(ctx))).map(_.foldLeft(CheckJsObject.empty) {
      case (result, other: CheckJsObject) =>
        result.copy(required = result.required ++ other.required, fields = result.fields ++ other.fields)
      case (result, _) => result
    })

  override def verify(context: SwaggerChecks, path: JsonPath, node: CheckJsValue): ValidationResult =
    schemas.foldLeft(ValidationResult.success) {
      (result, schema) =>
        result.combine(schema.verify(context, path, node))
    }
}
