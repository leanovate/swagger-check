package de.leanovate.swaggercheck.fixtures.model

import java.util.UUID

import de.leanovate.swaggercheck.generators.Generators
import org.scalacheck.{Arbitrary, Gen}
import play.api.libs.json.Json

case class SubBase(
                    id: UUID,
                    email: Option[String]
                    )

object SubBase {
  implicit val jsonFormat = Json.format[SubBase]

  implicit val arbitrary = Arbitrary(for {
    id <- Gen.uuid
    email <- Gen.option(Generators.email)
  } yield SubBase(id, email))
}