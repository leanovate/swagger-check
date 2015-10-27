package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.leanovate.swaggercheck.{VerifyResult, SwaggerChecks}
import org.scalacheck.Gen

@JsonDeserialize
case class ReferenceDefinition(
                                @JsonProperty("$ref")
                                ref: String
                                ) extends SchemaObject {
  def simpleRef: String = if (ref.startsWith("#/definitions/")) ref.substring(14) else ref

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = {
    ctx.swaggerAPI.definitions.get(simpleRef)
      .map(_.generate(ctx))
      .getOrElse(throw new RuntimeException(s"Referenced model does not exists: $simpleRef"))
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    ctx.swaggerAPI.definitions.get(simpleRef)
      .map(_.verify(ctx, path, node))
      .getOrElse(throw new RuntimeException(s"Referenced model does not exists: $simpleRef"))
  }
}
