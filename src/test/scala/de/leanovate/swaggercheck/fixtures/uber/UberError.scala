package de.leanovate.swaggercheck.fixtures.uber

import play.api.libs.json.Json

case class UberError(
                  code: Option[Int],
                  message: Option[String],
                  fields: Option[String]
                  )

object UberError {
  implicit val jsonFormat = Json.format[UberError]
}