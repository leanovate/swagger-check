package de.leanovate.swaggercheck.schema.jackson

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import de.leanovate.swaggercheck.schema.model._

@JsonDeserialize(builder = classOf[DefinitionBuilder])
trait DefinitionMixin {

}

class DefinitionBuilder @JsonCreator()(
                                        @JsonProperty("type") schemaType: Option[String] = None,
                                        @JsonProperty("allOf") allOf: Option[Seq[Definition]] = None,
                                        @JsonProperty("enum") enum: Option[List[String]] = None,
                                        @JsonProperty("exclusiveMinimum") exclusiveMinimum: Option[Boolean] = None,
                                        @JsonProperty("exclusiveMaximum") exclusiveMaximum: Option[Boolean] = None,
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
                                        @JsonProperty("additionalProperties") additionalPropertiesNode: Option[JsonNode] = None,
                                        @JsonProperty("required") required: Option[Set[String]] = None,
                                        @JsonProperty("$ref") ref: Option[String] = None,
                                        @JsonProperty("uniqueItems") uniqueItems: Option[Boolean] = None) {
  val additionalProperties = additionalPropertiesNode.map {
    case node if node.isBoolean => Left(node.isBinary)
    case node => Right(DefinitionBuilder.mapper.treeToValue(node, classOf[Definition]))
  }

  def build(): Definition = {
    Definition.build(
      schemaType,
      allOf,
      enum,
      exclusiveMinimum,
      exclusiveMaximum,
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
      ref,
      uniqueItems
    )
  }
}

object DefinitionBuilder {
  val mapper = new ObjectMapper().registerModule(DefaultScalaModule).registerModule(JsonSchemaModule)
}
