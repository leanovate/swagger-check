package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter


case class IntegerDefinition(
                              format: Option[String],
                              minimum: Option[BigInt],
                              maximum: Option[BigInt]
                            ) extends SchemaObject {
  override def validate[T](model: SchemaModel, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = {
    nodeAdapter.asInteger(node) match {
      case Some(value) =>
        if (minimum.exists(_ > value))
          ValidationResult.error(s"'$value' has to be greater than ${minimum.mkString} in path $path")
        else if (maximum.exists(_ < value))
          ValidationResult.error(s"'$value' has to be less than ${maximum.mkString} in path $path")
        else
          format.flatMap(model.getIntegerFormat).map(_.validate(path, value)).getOrElse(ValidationResult.success)
      case _ =>
        ValidationResult.error(s"$node should be an integer in path $path")

    }
  }
}