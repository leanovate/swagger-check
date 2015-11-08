package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator

/**
  * Json number.
  *
  * @param min optional minimum for shrinking
  * @param value the number value
  */
case class CheckJsNumber(
                          min: Option[BigDecimal],
                          max: Option[BigDecimal],
                          value: BigDecimal
                        ) extends CheckJsValue {
  override def asText(default: String): String = value.toString

  override def generate(json: JsonGenerator): Unit = json.writeNumber(value.underlying())

  override def shrink: Stream[CheckJsValue] = Stream.empty
}

object CheckJsNumber {
  def fixed(value: BigDecimal) = CheckJsNumber(Some(value), Some(value), value)
}