package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator

/**
  * An unformatted (arbitrary) json string.
  *
  * @param minLength optional minLength for shrinking
  * @param value the string value
  */
case class JsUnformattedString(
                                minLength: Option[Int],
                                value: String
                              ) extends JsValue {
  override def generate(json: JsonGenerator): Unit = json.writeString(value)
}
