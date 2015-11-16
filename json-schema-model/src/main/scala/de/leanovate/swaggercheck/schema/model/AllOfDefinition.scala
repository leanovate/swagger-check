package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

/**
  * Validates a sequence of schema objects.
  *
  * Will be only valid if all elements are valid.
  */
case class AllOfDefinition(definitions: Seq[Definition]) extends Definition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = {
    definitions.foldLeft(ValidationResult.success) {
      (result, definition) =>
        result.combine(definition.validate(schema, path, node))
    }
  }
}
