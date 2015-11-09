package models

import play.api.libs.json.Json

case class Link(href: String)

object Link {
  implicit val jsonWries = Json.writes[Link]
}