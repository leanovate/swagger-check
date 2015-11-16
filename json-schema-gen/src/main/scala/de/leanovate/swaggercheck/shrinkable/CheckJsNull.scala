package de.leanovate.swaggercheck.shrinkable

import com.fasterxml.jackson.core.JsonGenerator

case object CheckJsNull extends CheckJsValue {
  override def isNull: Boolean = true

  override def generate(json: JsonGenerator): Unit = json.writeNull()

  override def shrink: Stream[CheckJsValue] = Stream.empty
}