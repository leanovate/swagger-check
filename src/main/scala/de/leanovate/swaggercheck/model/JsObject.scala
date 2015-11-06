package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator

/**
  * Json object.
  *
  * @param required optional set of required fields for shrinking
  * @param order optional order of fields
  * @param fields the fields of the object
  */
case class JsObject(
                     required: Set[String],
                     order: Option[Seq[String]],
                     fields: Map[String, JsValue]
                   ) extends JsValue {
  override def generate(json: JsonGenerator): Unit = {
    json.writeStartObject()
    order match {
      case Some(fieldNames) => fieldNames.foreach {
        name =>
          json.writeFieldName(name)
          fields(name).generate(json)
      }
      case None =>
        fields.foreach {
          case (key, value) =>
            json.writeFieldName(key)
            value.generate(json)
        }
    }
    json.writeEndObject()
  }
}

object JsObject {
  /**
    * Create a fixed json object that will not shrink.
    */
  def fixed(fields: Seq[(String, JsValue)]): JsObject =
    JsObject(fields.map(_._1).toSet, Some(fields.map(_._1)), fields.toMap)
}
