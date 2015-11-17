package de.leanovate.swaggercheck.schema.play.model

import play.api.libs.json.Json

case class GeoLocation(
                        latitude: Option[Double],
                        longitude: Option[Double]
                      )

object GeoLocation {
  implicit val jsonFormat = Json.format[GeoLocation]
}