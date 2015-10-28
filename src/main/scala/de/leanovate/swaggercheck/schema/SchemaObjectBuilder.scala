package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}

class SchemaObjectBuilder @JsonCreator()(
                                          @JsonProperty("type") `type`: Option[String],
                                          @JsonProperty("enum") enum: Option[List[String]],
                                          @JsonProperty("format") format: Option[String],
                                          @JsonProperty("items") items: Option[SchemaObject],
                                          @JsonProperty("minItems") minItems: Option[Int],
                                          @JsonProperty("maxItems") maxItems: Option[Int],
                                          @JsonProperty("minimum") minimum: Option[BigDecimal],
                                          @JsonProperty("maximum") maximum: Option[BigDecimal],
                                          @JsonProperty("minLength") minLength: Option[Int],
                                          @JsonProperty("maxLength") maxLength: Option[Int],
                                          @JsonProperty("pattern") pattern: Option[String],
                                          @JsonProperty("properties") properties: Option[Map[String, SchemaObject]],
                                          @JsonProperty("required") required: Option[Set[String]],
                                          @JsonProperty("$ref") ref: Option[String]) {


  def build(): SchemaObject = {
    `type` match {
      case Some("object") => ObjectDefinition(required, properties, format)
      case Some("array") => ArrayDefinition(minItems, maxItems, items)
      case Some("string") => StringDefinition(format, minLength, maxLength, pattern, enum)
      case Some("integer") => IntegerDefinition(format, minimum.map(_.longValue()), maximum.map(_.longValue()))
      case Some("number") => NumberDefinition(format, minimum.map(_.doubleValue()), maximum.map(_.doubleValue()))
      case Some("boolean") => BooleanDefinition
      case _ if ref.isDefined => ReferenceDefinition(ref.get)
      case _ => EmptyDefinition
    }
  }
}
