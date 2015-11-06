package de.leanovate.swaggercheck.model

/**
  * Json integer.
  *
  * @param min Optional minimum for shrinking
  * @param value the integer value
  */
case class JsInteger(
                      min: Option[BigInt],
                      value: BigInt
                    ) extends JsValue

object JsInteger {
  /**
    * Get a fixed json integer that will not shrink.
    */
  def fixed(value: BigInt) = JsInteger(Some(value), value)
}