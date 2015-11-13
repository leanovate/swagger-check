package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

case class ObjectDefinition(
                             required: Option[Set[String]],
                             properties: Option[Map[String, Definition]],
                             additionalProperties: Option[Definition]
                           ) extends Definition {
  override def validate[T](model: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = {
    nodeAdapter.asObject(node) match {
      case Some(fields) =>
        properties.map {
          props =>
            props.foldLeft(ValidationResult.success) {
              case (result, (name, schema)) =>
                val field = fields.getOrElse(name, nodeAdapter.createNull)
                if (!nodeAdapter.isNull(field) || required.exists(_.contains(name)))
                  result.combine(schema.validate(model, path.field(name), field))
                else
                  ValidationResult.success
            }
        }.getOrElse(ValidationResult.success)
      case _ =>
        ValidationResult.error(s"$node should be an object in path $path")
    }
  }
}
