package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.model.{JsonPath, Schema, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsNull, CheckJsValue}
import org.scalacheck.Gen

object GeneratableEmpty extends GeneratableDefinition {
  override def generate(schema: GeneratableSchema): Gen[CheckJsValue] = Gen.const(CheckJsNull)

  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult[T] = ValidationResult.success(node)
}
