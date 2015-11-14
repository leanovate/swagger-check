package de.leanovate.swaggercheck.fixtures.model

import org.scalacheck.{Arbitrary, Gen}
import play.api.libs.json.Json

case class ThingList(
                      things: Seq[Thing]
                      )

object ThingList {
  implicit val jsonFormat = Json.format[ThingList]

  implicit val arbitrary = Arbitrary(for {
    things <- Gen.listOf(Arbitrary.arbitrary[Thing])
  } yield ThingList(things))
}