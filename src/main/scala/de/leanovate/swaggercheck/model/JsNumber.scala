package de.leanovate.swaggercheck.model

/**
  * Json number.
  *
  * @param min optional minimum for shrinking
  * @param value the number value
  */
case class JsNumber(
                     min: Option[BigDecimal],
                     value: BigDecimal
                   ) extends JsValue

object JsNumber {
  def fixed(value: BigDecimal) = JsNumber(Some(value),value)
}