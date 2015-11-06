package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator

/**
  * Json array.
  *
  * @param minSize optional minSize fro shrinking
  * @param elements elements of the array
  */
case class JsArray(
                    minSize: Option[Int],
                    elements: Seq[JsValue]
                  ) extends JsValue {
  override def generate(json: JsonGenerator): Unit = {
    json.writeStartArray()
    elements.foreach(_.generate(json))
    json.writeEndArray()
  }
}

object JsArray {
  /**
    * Get a fixed json array that will not shrink.
    */
  def fixed(elements: Seq[JsValue]) = JsArray(Some(elements.size), elements)
}