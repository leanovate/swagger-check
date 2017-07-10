package de.leanovate.swaggercheck.schema.jackson

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.leanovate.swaggercheck.schema.model._

@JsonDeserialize(builder = classOf[ParameterBuilder])
trait ParameterMixin {

}

class ParameterBuilder @JsonCreator()(
                                       @JsonProperty("name") name: String,
                                       @JsonProperty("in") in: String,
                                       @JsonProperty("required") required: Option[Boolean] = None,
                                       @JsonProperty("type") schemaType: Option[String] = None,
                                       @JsonProperty("schema") schema: Option[Definition] = None,
                                       @JsonProperty("format") format: Option[String] = None,
                                       @JsonProperty("allowEmptyValue") allowEmptyValue: Option[Boolean] = None,
                                       @JsonProperty("items") items: Option[Definition] = None,
                                       @JsonProperty("maximum") maximum: Option[BigDecimal] = None,
                                       @JsonProperty("exclusiveMaximum") exclusiveMaximum: Option[Boolean] = None,
                                       @JsonProperty("minimum") minimum: Option[BigDecimal] = None,
                                       @JsonProperty("exclusiveMinimum") exclusiveMinimum: Option[Boolean] = None,
                                       @JsonProperty("maxLength") maxLength: Option[Int] = None,
                                       @JsonProperty("minLength") minLength: Option[Int] = None,
                                       @JsonProperty("pattern") pattern: Option[String] = None,
                                       @JsonProperty("maxItems") maxItems: Option[Int] = None,
                                       @JsonProperty("minItems") minItems: Option[Int] = None,
                                       @JsonProperty("uniqueItems") uniqueItems: Option[Boolean] = None,
                                       @JsonProperty("enum") enum: Option[List[String]] = None) {


  def build(): Parameter = {
    Parameter.build(
      name,
      in,
      required,
      schema,
      schemaType,
      format,
      allowEmptyValue,
      items,
      maximum,
      exclusiveMaximum,
      minimum,
      exclusiveMinimum,
      maxLength,
      minLength,
      pattern,
      maxItems,
      minItems,
      uniqueItems,
      enum
    )
  }
}