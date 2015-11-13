package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

case object BooleanDefinition extends SchemaObject {
  override def validate[T](model: SchemaModel, path: Seq[String], node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = ???
}