package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

/**
  * Element of a json schema model.
  */
trait Definition {
  /**
    * Validate a json node versus this schema object.
    *
    * @param schema the overall model
    * @param path the current json path elements
    * @param node the node to validate
    * @param nodeAdapter adapter for the various json node implementations one might find
    * @return validation result
    */
  def validate[T](schema: Schema, path: JsonPath, node: T)(implicit nodeAdapter: NodeAdapter[T]): ValidationResult
}
