package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator

case object CheckJsNull extends CheckJsValue {
  override def generate(json: JsonGenerator): Unit = json.writeNull()

  override def shrink: Stream[CheckJsValue] = Stream.empty
}