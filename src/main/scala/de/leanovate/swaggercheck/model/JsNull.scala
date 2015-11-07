package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator

case object JsNull extends JsValue {
  override def generate(json: JsonGenerator): Unit = json.writeNull()

  override def shrink: Stream[JsValue] = Stream.empty
}