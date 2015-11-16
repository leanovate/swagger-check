package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

case class ObjectDefinition(
                             required: Option[Set[String]],
                             properties: Option[Map[String, Definition]],
                             additionalProperties: Option[Definition]
                           ) extends Definition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = {
    nodeAdapter.asObject(node) match {
      case Some(fields) =>
        val propertiesResult = properties.map {
          props =>
            props.foldLeft(ValidationResult.success) {
              case (result, (name, defintion)) =>
                val field = fields.getOrElse(name, nodeAdapter.createNull)
                if (!nodeAdapter.isNull(field) || required.exists(_.contains(name)))
                  result.combine(defintion.validate(schema, path.field(name), field))
                else
                  ValidationResult.success
            }
        }.getOrElse(ValidationResult.success)
        val additionalPropertiesResult = additionalProperties.map {
          definition =>
            val explicitFields = properties.map(_.keySet).getOrElse(Set.empty)
            fields.foldLeft(ValidationResult.success) {
              case (result, (name, field)) if explicitFields.contains(name) => result
              case (result, (name, field)) =>
                result.combine(definition.validate(schema, path.field(name), field))
            }
        }.getOrElse(ValidationResult.success)
        propertiesResult.combine(additionalPropertiesResult)
      case _ =>
        ValidationResult.error(s"$node should be an object in path $path")
    }
  }
}
