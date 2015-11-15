package de.leanovate.swaggercheck.schema.play

import de.leanovate.swaggercheck.schema.model.Definition
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

object DefinitionFormats {
  implicit lazy val jsonRead: Reads[Definition] =
    ((JsPath \ "type").readNullable[String] and
      (JsPath \ "allOf").lazyReadNullable(Reads.seq[Definition](jsonRead)) and
      (JsPath \ "enum").readNullable[List[String]] and
      (JsPath \ "format").readNullable[String] and
      (JsPath \ "items").lazyReadNullable(jsonRead) and
      (JsPath \ "minItems").readNullable[Int] and
      (JsPath \ "maxItems").readNullable[Int] and
      (JsPath \ "minimum").readNullable[BigDecimal] and
      (JsPath \ "maximum").readNullable[BigDecimal] and
      (JsPath \ "minLength").readNullable[Int] and
      (JsPath \ "maxLength").readNullable[Int] and
      (JsPath \ "oneOf").lazyReadNullable(Reads.seq[Definition](jsonRead)) and
      (JsPath \ "pattern").readNullable[String] and
      (JsPath \ "properties").lazyReadNullable(Reads.map[Definition](jsonRead)) and
      (JsPath \ "additionalProperties").lazyReadNullable(jsonRead) and
      (JsPath \ "required").readNullable[Set[String]] and
      (JsPath \ "$ref").readNullable[String]) (Definition.build _)
}
