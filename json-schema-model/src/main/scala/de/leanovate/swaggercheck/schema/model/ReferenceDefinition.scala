package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

case class ReferenceDefinition(
                                ref: String
                              ) extends Definition {
  override def validate[T](model: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = {
    model.findByRef(ref)
      .map(_.validate(model, path, node))
      .getOrElse(ValidationResult.error(s"Referenced model does not exists: $ref"))

  }
}
