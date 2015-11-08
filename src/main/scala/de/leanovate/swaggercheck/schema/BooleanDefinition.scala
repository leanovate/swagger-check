package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.model.{CheckJsBoolean, CheckJsValue}
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

object BooleanDefinition extends SchemaObject {

  override def generate(ctx: SwaggerChecks): Gen[CheckJsValue] =
    Gen.oneOf(CheckJsBoolean(true), CheckJsBoolean(false))

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: CheckJsValue): VerifyResult = node match {
    case CheckJsBoolean(_) =>
      VerifyResult.success
    case _ =>
      VerifyResult.error(s"$node should be a boolean: ${path.mkString(".")}")
  }
}
