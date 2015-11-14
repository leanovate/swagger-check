package de.leanovate.swaggercheck.fixtures.model

import java.util.UUID

import org.scalacheck.{Arbitrary, Gen}
import play.api.libs.json.Json

case class OtherBase(
                      id: UUID,
                      firstName: Option[String],
                      lastName: String
                      )

object OtherBase {
  implicit val jsonFormat = Json.format[OtherBase]

  implicit val arbitrary = Arbitrary(for {
    id <- Gen.uuid
    firstName <- Arbitrary.arbitrary[Option[String]]
    lastName <- Arbitrary.arbitrary[String]
  } yield OtherBase(id, firstName, lastName))
}