package models

import play.api.libs.json.Json

case class Error(
                  code: Int,
                  message: String
                )

object Error {
  implicit val jsonFormat = Json.format[Error]
}