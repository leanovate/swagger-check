package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

/**
  * Element of a json schema model.
  */
trait SchemaObject {
  /**
    * Validate a json node versus this schema object.
    *
    * @param model the overall model
    * @param path the current json path elements
    * @param node the node to validate
    * @param nodeAdapter adapter for the various json node implementations one might find
    * @return validation result
    */
  def validate[T](model: SchemaModel, path: Seq[String], node: T)(implicit nodeAdapter: NodeAdapter[T]): ValidationResult
}
