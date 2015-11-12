package models

import play.api.libs.json.{JsString, JsResult, JsValue, Format}

object ThingType extends Enumeration {
  type Type = Value
  val Primary, Secondary, Other = Value

  implicit val jsonFormat = new Format[Type] {
    override def reads(json: JsValue): JsResult[Type] =
      json.validate[String].map(ThingType.withName)

    override def writes(o: Type): JsValue = JsString(o.toString)
  }
}
