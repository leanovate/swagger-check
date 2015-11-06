package de.leanovate.swaggercheck.model

/**
  * Json object.
  *
  * @param required optional set of required fields for shrinking
  * @param fields the fields of the object
  */
case class JsObject(
                     required: Set[String],
                     fields: Map[String, JsValue]
                   ) extends JsValue

object JsObject {
  /**
    * Create a fixed json object that will not shrink.
    */
  def fixed(fields: Map[String, JsValue]): JsObject = JsObject(fields.keySet, fields)
}
