package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.model.{IntegerDefinition, JsonPath, Schema, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsInteger, CheckJsValue}
import org.scalacheck.{Arbitrary, Gen}

case class GeneratableInteger(
                               definition: IntegerDefinition
                             ) extends GeneratableDefinition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult =
    definition.validate(schema, path, node)

  override def generate(schema: GeneratableSchema): Gen[CheckJsValue] = {
    val generator: Gen[BigInt] =
      definition.format
        .flatMap(schema.findGeneratableIntegerFormat)
        .map(_.generate)
        .getOrElse(Arbitrary.arbitrary[BigInt])
        .suchThat {
          value: BigInt =>
            !definition.minimum.exists(_ > value) && !definition.maximum.exists(_ < value)
        }
    generator.map(value => CheckJsInteger(definition.minimum, definition.maximum, value))
  }
}
