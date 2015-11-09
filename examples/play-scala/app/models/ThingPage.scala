package models

import play.api.libs.json.Json

case class ThingPage(
                      things: Seq[Thing],
                      _links: ThingPageLinks
                    )

case class ThingPageLinks(
                           self: Link,
                           first: Option[Link],
                           last: Option[Link]
                         )

object ThingPage {
  implicit val linksFormat = Json.format[ThingPageLinks]

  implicit val jsonFormat = Json.format[ThingPage]
}