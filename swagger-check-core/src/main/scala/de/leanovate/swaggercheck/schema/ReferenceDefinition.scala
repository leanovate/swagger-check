package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.JsonProperty
import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.ValidationResult
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Gen

case class ReferenceDefinition(
                                @JsonProperty("$ref")
                                ref: String
                                ) extends SchemaObject {
  def simpleRef: String = if (ref.startsWith("#/definitions/")) ref.substring(14) else ref

  override def generate(ctx: SwaggerChecks): Gen[CheckJsValue] = {
    ctx.swaggerAPI.definitions.get(simpleRef)
      .map(_.generate(ctx))
      .getOrElse(throw new RuntimeException(s"Referenced model does not exists: $simpleRef"))
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: CheckJsValue): ValidationResult = {
    ctx.swaggerAPI.definitions.get(simpleRef)
      .map(_.verify(ctx, path, node))
      .getOrElse(throw new RuntimeException(s"Referenced model does not exists: $simpleRef"))
  }
}
