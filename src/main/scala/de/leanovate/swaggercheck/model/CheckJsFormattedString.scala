package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator

/**
  * A json string that is formatted according to some rule.
  *
  * Formatted values will not shrink.
  */
case class CheckJsFormattedString(
                              value: String
                            ) extends CheckJsValue {
  override def generate(json: JsonGenerator): Unit = json.writeString(value)

  override def shrink: Stream[CheckJsFormattedString] = Stream.empty
}
