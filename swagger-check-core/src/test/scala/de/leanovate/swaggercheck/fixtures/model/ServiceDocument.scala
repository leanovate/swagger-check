package de.leanovate.swaggercheck.fixtures.model

import org.scalacheck.Arbitrary
import play.api.libs.json.Json

case class ServiceDocumentLinks(
                                 self: Link,
                                 things: Link
                                 )

object ServiceDocumentLinks {
  implicit val jsonFormat = Json.format[ServiceDocumentLinks]

  implicit val arbitrary = Arbitrary(for {
    self <- Arbitrary.arbitrary[Link]
    things <- Arbitrary.arbitrary[Link]
  } yield ServiceDocumentLinks(self, things))
}

case class ServiceDocument(
                            _links: ServiceDocumentLinks
                            )

object ServiceDocument {
  implicit val jsonFormat = Json.format[ServiceDocument]

  implicit val arbitrary = Arbitrary(for {
    _links <- Arbitrary.arbitrary[ServiceDocumentLinks]
  } yield ServiceDocument(_links))
}
