package models

import play.api.libs.json.{Format, JsResult, JsString, JsValue}
import play.api.mvc.QueryStringBindable.Parsing

object ThingType extends Enumeration {
  type Type = Value
  val Primary, Secondary, Other = Value

  implicit val jsonFormat = new Format[Type] {
    override def reads(json: JsValue): JsResult[Type] =
      json.validate[String].map(ThingType.withName)

    override def writes(o: Type): JsValue = JsString(o.toString)
  }

  implicit val queryBinder = new Parsing[Type](
    parse = ThingType.withName,
    serialize = v => v.toString,
    error = (key: String, e: Exception) => "Cannot parse parameter %s as ThingType: %s".format(key, e.getMessage)
  )
}
