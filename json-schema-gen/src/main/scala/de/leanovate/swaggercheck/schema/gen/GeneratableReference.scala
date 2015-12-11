package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.model.{JsonPath, ReferenceDefinition, Schema, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Gen

case class GeneratableReference(
                                 definition: ReferenceDefinition
                               ) extends GeneratableDefinition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult[T] =
    definition.validate(schema, path, node)

  override def generate(schema: GeneratableSchema): Gen[CheckJsValue] = {
    schema.findByRef(definition.ref).map(_.generate(schema))
      .getOrElse(throw new RuntimeException(s"Referenced definition does not exists: ${definition.ref}"))
  }
}
