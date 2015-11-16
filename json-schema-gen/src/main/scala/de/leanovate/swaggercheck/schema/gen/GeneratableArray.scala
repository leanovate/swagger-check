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
    val min = definition.minItems.getOrElse(0)
    val max = Math.min(min + schema.maxItems, definition.maxItems.getOrElse(min + schema.maxItems))

    if (max == 0)
      CheckJsArray(definition.minItems, Seq.empty)
    else {
      val itemGenerator = definition.items.map(_.generate(schema.childContext)).getOrElse(schema.arbitraryValue)
      for {
        size <- Gen.choose(min, max)
        elements <- Gen.listOfN(size, itemGenerator)
      } yield CheckJsArray(definition.minItems, elements)
    }
  }
}
