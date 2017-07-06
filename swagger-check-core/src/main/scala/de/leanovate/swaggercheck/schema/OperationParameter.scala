package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.Operation.RequestBuilder
import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.jackson.DefinitionBuilder
import de.leanovate.swaggercheck.schema.model.Definition
import org.scalacheck.Gen

@JsonDeserialize(builder = classOf[OperationParameterBuilder])
case class OperationParameter(
                               name: Option[String],
                               in: String,
                               required: Boolean,
                               schema: Definition
                             ) {
  def applyTo(context: SwaggerChecks, builder: RequestBuilder): RequestBuilder = (name, in) match {
    case (Some(paramName), "path") =>
      builder.withPathParam(schema.generate(context)
        .map(value => Some(paramName -> value.asText("")))
        .suchThat(_.get._2.length > 0))
    case (Some(paramName), "query") if required =>
      builder.withQueryParam(schema.generate(context).map(value => Some(paramName -> value.asText(""))))
    case (Some(paramName), "query") =>
      builder.withQueryParam(Gen.option(schema.generate(context).map(value => paramName -> value.asText(""))))
    case (Some(headerName), "header") if required =>
      builder.withHeader(schema.generate(context).map(value => Some(headerName -> value.asText(""))))
    case (Some(headerName), "header") =>
      builder.withHeader(Gen.option(schema.generate(context).map(value => headerName -> value.asText(""))))
    case (_, "body") => builder.withBody(schema.generate(context))
    case _ => builder
  }
}

class ReferenceOperationParameter(globalRef: String) extends OperationParameter(None, "", false, null) {
  require(globalRef.startsWith("#/parameters/"), "Global parameters references need to start with #/parameters/")
  val ref = globalRef.substring(13)

  override def applyTo(context: SwaggerChecks, builder: RequestBuilder): RequestBuilder = throw new IllegalStateException("Not resolved ReferenceParameter")
}

object ReferenceOperationParameter {
  def unapply(parameter: ReferenceOperationParameter): Option[String] = Some(parameter.ref)
}

class OperationParameterBuilder @JsonCreator()(
                                                @JsonProperty("name") name: Option[String],
                                                @JsonProperty("$ref") ref: Option[String],
                                                @JsonProperty("in") in: String,
                                                @JsonProperty("required") required: Option[Boolean],
                                                @JsonProperty("type") schemaType: Option[String],
                                                @JsonProperty("format") format: Option[String],
                                                @JsonProperty("schema") schema: Option[Definition]
                                              ) {
  def build(): OperationParameter =
    ref match {
      case Some(reference) => new ReferenceOperationParameter(reference)
      case None => OperationParameter(
      name,
      in,
      required.getOrElse(false),
      schema.getOrElse(new DefinitionBuilder(schemaType = schemaType, format = format).build()))

  }
}