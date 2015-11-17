package de.leanovate.swaggercheck.schema.play.model

import play.api.libs.json.Json

case class Dimensions(
                       length: Double,
                       width: Double,
                       height: Double
                     )

object Dimensions {
  implicit val jsonFormat = Json.format[Dimensions]
}