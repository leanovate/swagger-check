package de.leanovate.swaggercheck.fixtures.model

import org.scalacheck.{Arbitrary, Gen}
import play.api.libs.json.Json

case class Thing(
                  id: Long,
                  name: String
                  )

object Thing {
  implicit val jsonFormat = Json.format[Thing]

  implicit val arbitrary = Arbitrary(for {
    id <- Gen.posNum[Long]
    name <- Gen.identifier
  } yield Thing(id, name))
}