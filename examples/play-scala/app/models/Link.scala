package models

import play.api.libs.json.Json
import play.api.mvc.{RequestHeader, Call}

case class Link(href: String)

object Link {
  def fromCall(call: Call)(implicit requestHeader: RequestHeader): Link = Link(call.absoluteURL())

  implicit val jsonWries = Json.writes[Link]
}