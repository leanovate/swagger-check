package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator

/**
  * Json object.
  *
  * @param required optional set of required fields for shrinking
  * @param fields the fields of the object
  */
case class JsObject(
                     required: Set[String],
                     fields: Map[String, JsValue]
                   ) extends JsValue {
  override def generate(json: JsonGenerator): Unit = {
    json.writeStartObject()
    fields.foreach {
      case (key, value) =>
        json.writeFieldName(key)
        value.generate(json)
    }
    json.writeEndObject()
  }
}

object JsObject {
  /**
    * Create a fixed json object that will not shrink.
    */
  def fixed(fields: Map[String, JsValue]): JsObject = JsObject(fields.keySet, fields)
}
