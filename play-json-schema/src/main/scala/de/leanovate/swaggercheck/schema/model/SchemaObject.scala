package de.leanovate.swaggercheck.schema.model

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

trait SchemaObject {
}

object SchemaObject {
  implicit lazy val jsonRead: Reads[SchemaObject] =
    ((JsPath \ "type").readNullable[String] and
      (JsPath \ "allOf").lazyReadNullable(Reads.seq[SchemaObject](jsonRead)) and
      (JsPath \ "enum").readNullable[Seq[String]] and
      (JsPath \ "format").readNullable[String] and
      (JsPath \ "items").lazyReadNullable(jsonRead) and
      (JsPath \ "minItems").readNullable[Int] and
      (JsPath \ "maxItems").readNullable[Int] and
      (JsPath \ "minimum").readNullable[BigDecimal] and
      (JsPath \ "maximum").readNullable[BigDecimal] and
      (JsPath \ "minLength").readNullable[Int] and
      (JsPath \ "maxLength").readNullable[Int] and
      (JsPath \ "pattern").readNullable[String] and
      (JsPath \ "properties").lazyReadNullable(Reads.map[SchemaObject](jsonRead)) and
      (JsPath \ "additionalProperties").lazyReadNullable(Reads.map[SchemaObject](jsonRead)) and
      (JsPath \ "required").readNullable[Set[String]] and
      (JsPath \ "$ref").readNullable[String]) (apply _)

  def apply(schemaType: Option[String],
            allOf: Option[Seq[SchemaObject]],
            enum: Option[Seq[String]],
            format: Option[String],
            items: Option[SchemaObject],
            minItems: Option[Int],
            maxItems: Option[Int],
            minimum: Option[BigDecimal],
            maximum: Option[BigDecimal],
            minLength: Option[Int],
            maxLength: Option[Int],
            pattern: Option[String],
            properties: Option[Map[String, SchemaObject]],
            additionalProperties: Option[Map[String, SchemaObject]],
            required: Option[Set[String]],
            reference: Option[String]): SchemaObject = ???
}