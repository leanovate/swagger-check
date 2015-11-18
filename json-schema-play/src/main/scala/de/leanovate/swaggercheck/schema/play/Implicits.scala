package de.leanovate.swaggercheck.schema.play

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.model.{DefaultSchema, Definition}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object Implicits {
  implicit lazy val definitionReads: Reads[Definition] =
    ((JsPath \ "type").readNullable[String] and
      (JsPath \ "allOf").lazyReadNullable(Reads.seq[Definition](definitionReads)) and
      (JsPath \ "enum").readNullable[List[String]] and
      (JsPath \ "exclusiveMinimum").readNullable[Boolean] and
      (JsPath \ "exclusiveMaximum").readNullable[Boolean] and
      (JsPath \ "format").readNullable[String] and
      (JsPath \ "items").lazyReadNullable(definitionReads) and
      (JsPath \ "minItems").readNullable[Int] and
      (JsPath \ "maxItems").readNullable[Int] and
      (JsPath \ "minimum").readNullable[BigDecimal] and
      (JsPath \ "maximum").readNullable[BigDecimal] and
      (JsPath \ "minLength").readNullable[Int] and
      (JsPath \ "maxLength").readNullable[Int] and
      (JsPath \ "oneOf").lazyReadNullable(Reads.seq[Definition](definitionReads)) and
      (JsPath \ "pattern").readNullable[String] and
      (JsPath \ "properties").lazyReadNullable(Reads.map[Definition](definitionReads)) and
      (JsPath \ "additionalProperties").lazyReadNullable(definitionReads) and
      (JsPath \ "required").readNullable[Set[String]] and
      (JsPath \ "$ref").readNullable[String] and
      (JsPath \ "uniqueItems").readNullable[Boolean]
      ) (Definition.build _)

  implicit lazy val schemaReads: Reads[DefaultSchema] =
    ((JsPath \ "type").readNullable[String] and
      (JsPath \ "allOf").readNullable[Seq[Definition]] and
      (JsPath \ "enum").readNullable[List[String]] and
      (JsPath \ "exclusiveMinimum").readNullable[Boolean] and
      (JsPath \ "exclusiveMaximum").readNullable[Boolean] and
      (JsPath \ "format").readNullable[String] and
      (JsPath \ "items").readNullable[Definition] and
      (JsPath \ "minItems").readNullable[Int] and
      (JsPath \ "maxItems").readNullable[Int] and
      (JsPath \ "minimum").readNullable[BigDecimal] and
      (JsPath \ "maximum").readNullable[BigDecimal] and
      (JsPath \ "minLength").readNullable[Int] and
      (JsPath \ "maxLength").readNullable[Int] and
      (JsPath \ "oneOf").readNullable[Seq[Definition]] and
      (JsPath \ "pattern").readNullable[String] and
      (JsPath \ "properties").readNullable[Map[String, Definition]] and
      (JsPath \ "additionalProperties").lazyReadNullable(definitionReads) and
      (JsPath \ "required").readNullable[Set[String]] and
      (JsPath \ "$ref").readNullable[String] and
      (JsPath \ "uniqueItems").readNullable[Boolean] and
      (JsPath \ "definitions").readNullable[Map[String, Definition]]
      ) (DefaultSchema.build _)

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
