package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator

case class CheckJsBoolean(value: Boolean) extends CheckJsValue {
  override def asText(default: String): String = value.toString

  override def generate(json: JsonGenerator): Unit = json.writeBoolean(value)

  override def shrink: Stream[CheckJsBoolean] = Stream.empty
}
