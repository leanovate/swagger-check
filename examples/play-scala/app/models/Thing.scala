package models

import java.util.UUID

import play.api.libs.json.Json

case class Thing(
                  id: UUID,
                  name: String,
                  thingType: ThingType.Type
                )

object Thing {
  implicit val jsonFormat = Json.format[Thing]
}