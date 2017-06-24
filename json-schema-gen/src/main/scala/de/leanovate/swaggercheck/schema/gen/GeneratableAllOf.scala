package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.model.{AllOfDefinition, JsonPath, Schema, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsObject, CheckJsValue}
import org.scalacheck.Gen
import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import scala.collection.JavaConverters._

case class GeneratableAllOf(
                             definition: AllOfDefinition
                           ) extends GeneratableDefinition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult =
    definition.validate(schema, path, node)

  override def generate(schema: GeneratableSchema): Gen[CheckJsValue] =
    Gen.sequence(definition.definitions.map(_.generate(schema))).map(_.asScala.foldLeft(CheckJsObject.empty) {
      case (result, other: CheckJsObject) =>
        result.copy(required = result.required ++ other.required, fields = result.fields ++ other.fields)
      case (result, _) => result
    })
}
