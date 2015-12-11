package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

case object BooleanDefinition extends Definition {
  override def validate[T](model: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult[T] = {
    nodeAdapter.asBoolean(node) match {
      case Some(_) =>
        ValidationResult.success(node)
      case _ =>
        ValidationResult.error(s"$node should be a boolean in path $path")
    }
  }
}