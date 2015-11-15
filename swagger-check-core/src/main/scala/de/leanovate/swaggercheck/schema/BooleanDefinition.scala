package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.ValidationResult
import de.leanovate.swaggercheck.shrinkable.{CheckJsBoolean, CheckJsValue}
import org.scalacheck.Gen

object BooleanDefinition extends SchemaObject {

  override def generate(ctx: SwaggerChecks): Gen[CheckJsValue] =
    Gen.oneOf(CheckJsBoolean(true), CheckJsBoolean(false))

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: CheckJsValue): ValidationResult = node match {
    case CheckJsBoolean(_) =>
      ValidationResult.success
    case _ =>
      ValidationResult.error(s"$node should be a boolean: ${path.mkString(".")}")
  }
}
