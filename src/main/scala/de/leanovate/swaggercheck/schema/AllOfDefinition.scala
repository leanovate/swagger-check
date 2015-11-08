package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.model.{CheckJsObject, CheckJsValue}
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

import scala.collection.JavaConversions._

case class AllOfDefinition(schemas: Seq[SchemaObject]) extends SchemaObject {

  override def generate(ctx: SwaggerChecks): Gen[CheckJsValue] =
    Gen.sequence(schemas.map(_.generate(ctx))).map(_.foldLeft(CheckJsObject.empty) {
      case (result, other: CheckJsObject) =>
        result.copy(required = result.required ++ other.required, fields = result.fields ++ other.fields)
      case (result, _) => result
    })

  override def verify(context: SwaggerChecks, path: Seq[String], node: CheckJsValue): VerifyResult =
    schemas.foldLeft(VerifyResult.success) {
      (result, schema) =>
        result.combine(schema.verify(context, path, node))
    }
}
