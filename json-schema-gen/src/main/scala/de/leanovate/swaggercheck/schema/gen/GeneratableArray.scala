package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.model.{ValidationResult, JsonPath, Schema, ArrayDefinition}
import de.leanovate.swaggercheck.shrinkable.{CheckJsArray, CheckJsValue}
import org.scalacheck.Gen

import GeneratableDefinition._

case class GeneratableArray(
                           definition: ArrayDefinition
                         ) extends GeneratableDefinition {
  override def validate[T](model: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = definition.validate(model, path, node)

  override def generate(model: GeneratableSchema): Gen[CheckJsValue] = {
    definition.items.map {
      itemsSchema =>
        val min = definition.minItems.getOrElse(0)
        val max = Math.min(min + model.maxItems, definition.maxItems.getOrElse(min + model.maxItems))
        for {
          size <- Gen.choose(min, max)
          elements <- Gen.listOfN(size, itemsSchema.generate(model.childContext))
        } yield CheckJsArray(definition.minItems, elements)
    }.getOrElse(model.arbitraryArray)
  }
}
