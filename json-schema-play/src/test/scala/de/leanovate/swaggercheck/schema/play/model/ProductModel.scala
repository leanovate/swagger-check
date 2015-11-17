package de.leanovate.swaggercheck.schema.play.model

import play.api.libs.json.Json

case class ProductModel(
                         id: Long,
                         name: String,
                         price: BigDecimal,
                         tags: Option[Seq[String]],
                         dimensions: Option[Dimensions],
                         warehouseLocation: Option[GeoLocation]
                       )

object ProductModel {
  implicit val jsonFormat = Json.format[ProductModel]
}