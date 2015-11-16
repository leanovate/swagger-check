package de.leanovate.swaggercheck.schema.play

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import play.api.libs.json._

object JsValueNodeAdapter {
  implicit val adapter = new NodeAdapter[JsValue] {
    override def asArray(node: JsValue): Option[Seq[JsValue]] = node match {
      case JsArray(value) => Some(value)
      case _ => None
    }

    override def asNumber(node: JsValue): Option[BigDecimal] = node match {
      case JsNumber(value) => Some(value)
      case _ => None
    }

    override def asString(node: JsValue): Option[String] = node match {
      case JsString(value) => Some(value)
      case _ => None
    }

    override def asBoolean(node: JsValue): Option[Boolean] = node match {
      case JsBoolean(value) => Some(value)
      case _ => None
    }

    override def asInteger(node: JsValue): Option[BigInt] = node match {
      case JsNumber(value) => value.toBigIntExact()
      case _ => None
    }

    override def createNull: JsValue = JsNull

    override def isNull(node: JsValue): Boolean = node == JsNull

    override def asObject(node: JsValue): Option[Map[String, JsValue]] = node match {
      case obj: JsObject => Some(obj.fields.toMap)
      case _ => None
    }
  }
}
