package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.model.{BooleanDefinition, JsonPath, Schema, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsBoolean, CheckJsValue}
import org.scalacheck.Gen

case object GeneratableBoolean extends GeneratableDefinition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult =
    BooleanDefinition.validate(schema, path, node)

  override def generate(schema: GeneratableSchema): Gen[CheckJsValue] =
    Gen.oneOf(true, false).map(CheckJsBoolean)
}
