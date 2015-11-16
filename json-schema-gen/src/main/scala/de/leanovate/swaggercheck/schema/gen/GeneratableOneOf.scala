package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.model._
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Gen

case class GeneratableOneOf(
                             definition: OneOfDefinition
                           ) extends GeneratableDefinition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult =
    definition.validate(schema, path, node)

  override def generate(schema: GeneratableSchema): Gen[CheckJsValue] = for {
    index <- Gen.choose(0, definition.definitions.length - 1)
    value <- definition.definitions(index).generate(schema)
  } yield value
}
