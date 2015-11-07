package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator

case class JsBoolean(value: Boolean) extends JsValue {
  override def generate(json: JsonGenerator): Unit = json.writeBoolean(value)

  override def shrink: Stream[JsBoolean] = Stream.empty
}
