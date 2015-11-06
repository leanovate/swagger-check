package de.leanovate.swaggercheck.model

import java.math.BigInteger

import com.fasterxml.jackson.core.JsonGenerator

/**
  * Json integer.
  *
  * @param min Optional minimum for shrinking
  * @param value the integer value
  */
case class JsInteger(
                      min: Option[BigInt],
                      value: BigInt
                    ) extends JsValue {
  override def generate(json: JsonGenerator): Unit = json.writeNumber(value.underlying())
}

object JsInteger {
  /**
    * Get a fixed json integer that will not shrink.
    */
  def fixed(value: BigInt) = JsInteger(Some(value), value)
}