package de.leanovate.swaggercheck.schema.jackson

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.leanovate.swaggercheck.schema.model._

@JsonDeserialize(builder = classOf[DefinitionBuilder])
trait DefinitionMixin {

}

class DefinitionBuilder @JsonCreator()(
                                        @JsonProperty("type") schemaType: Option[String] = None,
                                        @JsonProperty("allOf") allOf: Option[Seq[Definition]] = None,
                                        @JsonProperty("enum") enum: Option[List[String]] = None,
                                        @JsonProperty("format") format: Option[String] = None,
                                        @JsonProperty("items") items: Option[Definition] = None,
                                        @JsonProperty("minItems") minItems: Option[Int] = None,
                                        @JsonProperty("maxItems") maxItems: Option[Int] = None,
                                        @JsonProperty("minimum") minimum: Option[BigDecimal] = None,
                                        @JsonProperty("maximum") maximum: Option[BigDecimal] = None,
                                        @JsonProperty("minLength") minLength: Option[Int] = None,
                                        @JsonProperty("maxLength") maxLength: Option[Int] = None,
                                        @JsonProperty("oneOf") oneOf: Option[Seq[Definition]] = None,
                                        @JsonProperty("pattern") pattern: Option[String] = None,
                                        @JsonProperty("properties") properties: Option[Map[String, Definition]] = None,
                                        @JsonProperty("additionalProperties") additionalProperties: Option[Definition] = None,
                                        @JsonProperty("required") required: Option[Set[String]] = None,
                                        @JsonProperty("$ref") ref: Option[String] = None) {

  def build(): Definition = {
    Definition.build(
      schemaType,
      allOf,
      enum,
      format,
      items,
      minItems,
      maxItems,
      minimum,
      maximum,
      minLength,
      maxLength,
      oneOf,
      pattern,
      properties,
      additionalProperties,
      required,
      ref
    )
  }
}
