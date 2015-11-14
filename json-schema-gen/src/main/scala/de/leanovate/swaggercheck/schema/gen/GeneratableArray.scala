package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.model.{ArrayDefinition, JsonPath, Schema, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsArray, CheckJsValue}
import org.scalacheck.Gen

case class GeneratableArray(
                             definition: ArrayDefinition
                           ) extends GeneratableDefinition {
  override def validate[T](model: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = definition.validate(model, path, node)

  override def generate(schema: GeneratableSchema): Gen[CheckJsValue] = {
    definition.items.map {
      itemDefinition =>
        val min = definition.minItems.getOrElse(0)
        val max = Math.min(min + schema.maxItems, definition.maxItems.getOrElse(min + schema.maxItems))
        for {
          size <- Gen.choose(min, max)
          elements <- Gen.listOfN(size, itemDefinition.generate(schema.childContext))
        } yield CheckJsArray(definition.minItems, elements)
    }.getOrElse(schema.arbitraryArray)
  }
}
