package de.leanovate.swaggercheck.model

/**
  * A json string that is formatted according to some rule.
  *
  * Formatted values will not shrink.
  */
case class JsFormattedString(
                              value: String
                            ) extends JsValue
