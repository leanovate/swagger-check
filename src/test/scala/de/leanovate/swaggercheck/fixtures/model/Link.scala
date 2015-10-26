package de.leanovate.swaggercheck.fixtures.model

import de.leanovate.swaggercheck.Generators
import org.scalacheck.Arbitrary
import play.api.libs.json.Json

case class Link(
                 href: String
                 )

object Link {
  implicit val jsonFormat = Json.format[Link]

  implicit val arbitrary = Arbitrary(for {
    href <- Generators.url
  } yield Link(href))
}