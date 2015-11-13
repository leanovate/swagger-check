package de.leanovate.swaggercheck.schema.adapter

trait NodeAdapter[T] {
  /**
    * Extracts the elements of a json array.
    *
    * @param node the json node to extract
    * @return list of element nodes or `None` if `node` is not an array
    */
  def asArray(node: T): Option[Seq[T]]

  /**
    * Extract the boolean value of a node.
    *
    * @param node the json node to extract
    * @return boolean value of  `None` if `node` is not a boolean
    */
  def asBoolean(node: T) : Option[Boolean]
}
