package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

/**
  * Validates a sequence of schema objects.
  *
  * Will be valid if one elements is valid.
  */
case class OneOfDefinition(definitions: Seq[Definition]) extends Definition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = {
    val results = definitions.map(_.validate(schema, path, node))

    if (results.contains(ValidationResult.success))
      ValidationResult.success
    else
      results.foldLeft(ValidationResult.success) {
        (result, r) =>
          result.combine(r)
      }
  }
}
