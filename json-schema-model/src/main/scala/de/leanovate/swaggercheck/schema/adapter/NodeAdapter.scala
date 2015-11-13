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
  def asBoolean(node: T): Option[Boolean]

  /**
    * Extract the integer value of a node.
    *
    * @param node the json node to extract
    * @return integer value of  `None` if `node` is not an integer (floating points do not count as integer)
    */
  def asInteger(node: T): Option[BigInt]

  /**
    * Extract the number value of a node.
    *
    * @param node the json node to extract
    * @return number value of  `None` if `node` is not a number
    */
  def asNumber(node: T): Option[BigDecimal]

  /**
    * Extract the field of an object node
    *
    * @param node the json node to extract
    * @return map of all field or `None` if `node` is not an object
    */
  def asObject(node: T): Option[Map[String, T]]

  /**
    * Extract the string value of a node.
    *
    * @param node the json node to extract
    * @return string value of  `None` if `node` is not a string
    */
  def asString(node: T): Option[String]

  /**
    * Create a json null node
    */
  def createNull: T

  /**
    * Check if a node is null.
    */
  def isNull(node: T): Boolean
}
