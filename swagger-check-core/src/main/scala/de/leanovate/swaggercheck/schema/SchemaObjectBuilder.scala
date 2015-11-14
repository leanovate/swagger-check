package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}

class SchemaObjectBuilder @JsonCreator()(
                                          @JsonProperty("type") schemaType: Option[String] = None,
                                          @JsonProperty("allOf") allOf: Option[Seq[SchemaObject]] = None,
                                          @JsonProperty("enum") enum: Option[List[String]] = None,
                                          @JsonProperty("format") format: Option[String] = None,
                                          @JsonProperty("items") items: Option[SchemaObject] = None,
                                          @JsonProperty("minItems") minItems: Option[Int] = None,
                                          @JsonProperty("maxItems") maxItems: Option[Int] = None,
                                          @JsonProperty("minimum") minimum: Option[BigDecimal] = None,
                                          @JsonProperty("maximum") maximum: Option[BigDecimal] = None,
                                          @JsonProperty("minLength") minLength: Option[Int] = None,
                                          @JsonProperty("maxLength") maxLength: Option[Int] = None,
                                          @JsonProperty("pattern") pattern: Option[String] = None,
                                          @JsonProperty("properties") properties: Option[Map[String, SchemaObject]] = None,
                                          @JsonProperty("additionalProperties") additionalProperties: Option[SchemaObject] = None,
                                          @JsonProperty("required") required: Option[Set[String]] = None,
                                          @JsonProperty("$ref") ref: Option[String] = None) {

  def build(): SchemaObject = {
    allOf match {
      case Some(schemas) =>
        AllOfDefinition(schemas)
      case _ =>
        schemaType match {
          case Some("object") => ObjectDefinition(required, properties, additionalProperties)
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
}
