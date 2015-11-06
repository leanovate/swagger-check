package de.leanovate.swaggercheck.model

/**
  * An unformatted (arbitrary) json string.
  *
  * @param minLength optional minLength for shrinking
  * @param value the string value
  */
case class JsUnformattedString(
                                minLength: Option[Int],
                                value: String
                              ) extends JsValue
