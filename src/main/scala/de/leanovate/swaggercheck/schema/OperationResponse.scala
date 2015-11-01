package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}

@JsonDeserialize(builder = classOf[OperationResponseBuilder])
case class OperationResponse(
                              schema: Option[SchemaObject],
                              headers: Seq[(String, SchemaObject)]
                              ) {
  def verify(context: SwaggerChecks, requestHeader: Map[String, String], requestBody: String): VerifyResult = {
    val bodyVerify = schema.map {
      expected =>
        val tree = new ObjectMapper().readTree(requestBody)
        expected.verify(context, Nil, tree)
    }.getOrElse(VerifyResult.success)

    headers.foldLeft(bodyVerify) {
      case (result, (name, schema)) =>
        result.combine(
          requestHeader.get(name.toLowerCase)
            .map(value => schema.verify(context, Nil, JsonNodeFactory.instance.textNode(value)))
            .getOrElse(VerifyResult.success))
    }
  }
}

class OperationResponseBuilder @JsonCreator()(
                                               @JsonProperty("schema") schema: Option[SchemaObject],
                                               @JsonProperty("headers") headers: Option[Map[String, SchemaObject]]
                                               ) {
  def build(): OperationResponse = OperationResponse(schema, headers.map(_.toSeq).getOrElse(Seq.empty))
}