package support

import java.util.UUID

import de.leanovate.swaggercheck.Generators
import models._
import org.scalacheck.{Arbitrary, Gen}

trait Arbitraries {
  implicit val arbitraryUUID = Arbitrary[UUID](Gen.uuid)

  implicit val arbitraryThing = Arbitrary[Thing](for {
    id <- Arbitrary.arbitrary[UUID]
    name <- Arbitrary.arbitrary[String]
    thingType <- Gen.oneOf(ThingType.Primary, ThingType.Secondary, ThingType.Other)
  } yield Thing(id, name, thingType))

  implicit val arbitraryLink = Arbitrary[Link](for {
    href <- Generators.url
  } yield Link(href))

  implicit val arbitraryThingPageLinks = Arbitrary[ThingsPageLinks](for {
    self <- Arbitrary.arbitrary[Link]
    first <- Arbitrary.arbitrary[Option[Link]]
    last <- Arbitrary.arbitrary[Option[Link]]
  } yield ThingsPageLinks(self, first, last))

  implicit val arbitraryThingPage = Arbitrary[ThingsPage](for {
    size <- Gen.choose(0, 20)
    things <- Gen.listOfN(size, Arbitrary.arbitrary[Thing])
    _links <- Arbitrary.arbitrary[ThingsPageLinks]
  } yield ThingsPage(things, _links))
}
