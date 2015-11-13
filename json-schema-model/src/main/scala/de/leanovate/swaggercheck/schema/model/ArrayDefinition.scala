package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

case class ArrayDefinition(
                            minItems: Option[Int],
                            maxItems: Option[Int],
                            items: Option[SchemaObject]
                          ) extends SchemaObject {
  override def validate[T](model: SchemaModel, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = {
    nodeAdapter.asArray(node) match {
      case Some(elements) =>
        if (minItems.exists(_ > elements.size))
          ValidationResult.error(s"$node should have at least ${minItems.mkString} items in path $path")
        else if (maxItems.exists(_ < elements.size))
          ValidationResult.error(s"$node should have at least ${maxItems.mkString} items in path $path")
        else
          items.map {
            itemsSchema =>
              elements.zipWithIndex.foldLeft(ValidationResult.success) {
                case (result, (element, index)) =>
                  result.combine(itemsSchema.validate(model, path.index(index), element))
              }
          }.getOrElse(ValidationResult.success)
      case _ =>
        ValidationResult.error(s"$node should be an array in path $path")
    }
  }
}
