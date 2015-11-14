package de.leanovate.swaggercheck.fixtures.uber

import org.scalacheck.{Arbitrary, Gen}
import play.api.libs.json.Json

case class UberProduct(
                        product_id: Option[String],
                        description: Option[String],
                        display_name: Option[String],
                        capacity: Option[String],
                        image: Option[String]
                        )

object UberProduct {
  implicit val jsonFormat = Json.format[UberProduct]

  implicit val arbitrary = Arbitrary(for {
    product_id <- Gen.option(Gen.identifier)
    description <- Gen.option(Gen.alphaStr)
    display_name <- Gen.option(Gen.alphaStr)
    capacity <- Gen.option(Gen.alphaStr)
    image <- Gen.option(Gen.identifier)
  } yield UberProduct(product_id, description, display_name, capacity, image))
}
