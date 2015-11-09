package models

import java.util.UUID

import play.api.libs.json.Json

case class Thing(
                  id: UUID,
                  name: String
                )

object Thing {
  implicit val jsonFormat = Json.format[Thing]
}