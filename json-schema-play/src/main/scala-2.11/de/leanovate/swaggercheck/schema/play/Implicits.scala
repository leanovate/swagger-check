package de.leanovate.swaggercheck.schema.play

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.model.{DefaultSchema, Definition, Parameter}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object Implicits {
  lazy val additionalPropertiesDefinition: Reads[
    Option[Either[Boolean, Definition]]] =
    (JsPath \ "additionalProperties")
      .lazyReadNullable(definitionReads)
      .map(_.map(Right(_)))
  lazy val additionalPropertiesBoolean: Reads[
    Option[Either[Boolean, Definition]]] =
    (JsPath \ "additionalProperties").readNullable[Boolean].map(_.map(Left(_)))

  implicit lazy val definitionReads: Reads[Definition] =
    ((JsPath \ "type").readNullable[String] and
      (JsPath \ "allOf").lazyReadNullable(
        Reads.seq[Definition](definitionReads)) and
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
      (JsPath \ "oneOf").lazyReadNullable(
        Reads.seq[Definition](definitionReads)) and
      (JsPath \ "pattern").readNullable[String] and
      (JsPath \ "properties").lazyReadNullable(
        Reads.map[Definition](definitionReads)) and
      (additionalPropertiesBoolean | additionalPropertiesDefinition) and
      (JsPath \ "required").readNullable[Set[String]] and
      (JsPath \ "$ref").readNullable[String] and
      (JsPath \ "uniqueItems").readNullable[Boolean])(Definition.build _)

  implicit lazy val parameterReads: Reads[Parameter] =
    ((JsPath \ "name").read[String] and
      (JsPath \ "in").read[String] and
      (JsPath \ "required").readNullable[Boolean] and
      (JsPath \ "schema").lazyReadNullable(definitionReads) and
      (JsPath \ "type").readNullable[String] and
      (JsPath \ "format").readNullable[String] and
      (JsPath \ "allowEmptyValue").readNullable[Boolean] and
      (JsPath \ "items").lazyReadNullable(definitionReads) and
      (JsPath \ "maximum").readNullable[BigDecimal] and
      (JsPath \ "exclusiveMaximum").readNullable[Boolean] and
      (JsPath \ "minimum").readNullable[BigDecimal] and
      (JsPath \ "exclusiveMinimum").readNullable[Boolean] and
      (JsPath \ "maxLength").readNullable[Int] and
      (JsPath \ "minLength").readNullable[Int] and
      (JsPath \ "pattern").readNullable[String] and
      (JsPath \ "maxItems").readNullable[Int] and
      (JsPath \ "minItems").readNullable[Int] and
      (JsPath \ "uniqueItems").readNullable[Boolean] and
      (JsPath \ "enum").readNullable[List[String]]) (Parameter.build _)

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
      (additionalPropertiesBoolean | additionalPropertiesDefinition) and
      (JsPath \ "required").readNullable[Set[String]] and
      (JsPath \ "$ref").readNullable[String] and
      (JsPath \ "uniqueItems").readNullable[Boolean] and
      (JsPath \ "definitions").readNullable[Map[String, Definition]] and
      (JsPath \ "parameters").readNullable[Map[String, Parameter]]) (DefaultSchema.build _)

  implicit object Adapter extends NodeAdapter[JsValue] {
    override def asArray(node: JsValue): Option[Seq[JsValue]] =
      node.asOpt[Seq[JsValue]]

    override def asNumber(node: JsValue): Option[BigDecimal] =
      node.asOpt[BigDecimal]

    override def asString(node: JsValue): Option[String] = node.asOpt[String]

    override def asBoolean(node: JsValue): Option[Boolean] =
      node.asOpt[Boolean]

    override def asInteger(node: JsValue): Option[BigInt] =
      node.asOpt[BigDecimal].flatMap(_.toBigIntExact())

    override val createNull: JsValue = JsNull

    override def asObject(node: JsValue): Option[Map[String, JsValue]] =
      node.asOpt[Map[String, JsValue]]
  }
}
