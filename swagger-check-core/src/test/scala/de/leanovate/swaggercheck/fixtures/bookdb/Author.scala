package de.leanovate.swaggercheck.fixtures.bookdb

import java.util.UUID

import org.scalacheck.{Arbitrary, Gen}
import play.api.libs.json.Json

case class Author(
                   id: Option[UUID],
                   name: String
                 )

object Author {
  implicit val jsonFormat = Json.format[Author]

  implicit val arbitrary = Arbitrary(for {
    id <- Gen.option(Gen.uuid)
    name <- Arbitrary.arbitrary[String]
  } yield Author(id, name))
}