package de.leanovate.swaggercheck.fixtures.model

import play.api.libs.json.Json

case class ThingNode(
                      theThing: Thing,
                      children: Seq[ThingNode]
                      )

object ThingNode {
  implicit val jsonFormat = Json.format[ThingNode]
}