package models

import play.api.libs.json.Json

case class ThingsPage(
                       things: Seq[Thing],
                       _links: ThingsPageLinks
                     )

case class ThingsPageLinks(
                            self: Link,
                            first: Option[Link],
                            last: Option[Link]
                          )

object ThingsPage {
  implicit val linksWrite = Json.writes[ThingsPageLinks]

  implicit val jsonWrites = Json.writes[ThingsPage]
}