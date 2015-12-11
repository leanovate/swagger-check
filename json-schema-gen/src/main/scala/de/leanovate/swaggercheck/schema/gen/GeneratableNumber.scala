package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.model.{JsonPath, NumberDefinition, Schema, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsNumber, CheckJsValue}
import org.scalacheck.{Arbitrary, Gen}

import scala.util.Try

case class GeneratableNumber(
                              definition: NumberDefinition
                            ) extends GeneratableDefinition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult[T] =
    definition.validate(schema, path, node)

  override def generate(schema: GeneratableSchema): Gen[CheckJsValue] = {
    val generator: Gen[BigDecimal] =
      definition.format
        .flatMap(schema.findGeneratableNumberFormat)
        .map(_.generate)
        .getOrElse(Arbitrary.arbitrary[BigDecimal])
        .retryUntil {
          value: BigDecimal =>
            Try(BigDecimal(value.toString())).isSuccess &&
              !definition.minimum.exists(_ > value) && !definition.maximum.exists(_ < value)
        }
    generator.map(value => CheckJsNumber(definition.minimum, definition.maximum, value))
  }
}
