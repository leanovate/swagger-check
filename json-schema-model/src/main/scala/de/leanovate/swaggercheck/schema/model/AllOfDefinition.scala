package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

/**
  * Validates a sequence of schema objects.
  *
  * Will be only valid if all elements are valid.
  */
case class AllOfDefinition(schemas: Seq[SchemaObject]) extends SchemaObject {
  override def validate[T](model: SchemaModel, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = {
    schemas.foldLeft(ValidationResult.success) {
      (result, schema) =>
        result.combine(schema.validate(model, path, node))
    }
  }
}
