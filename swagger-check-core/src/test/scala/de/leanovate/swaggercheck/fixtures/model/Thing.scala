package de.leanovate.swaggercheck.fixtures.model

import org.scalacheck.{Arbitrary, Gen}
import play.api.libs.json.Json

case class Thing(
                  id: Long,
                  name: String,
                  description: Option[String]
                  )

object Thing {
  implicit val jsonFormat = Json.format[Thing]

  implicit val arbitrary = Arbitrary(for {
    id <- Gen.posNum[Long]
    name <- Gen.identifier
    description <- Gen.option(Gen.identifier)
  } yield Thing(id, name, description))
}