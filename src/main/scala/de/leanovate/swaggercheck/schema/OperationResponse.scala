package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.leanovate.swaggercheck.model.{CheckJsString, CheckJsValue}
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}

@JsonDeserialize(builder = classOf[OperationResponseBuilder])
case class OperationResponse(
                              schema: Option[SchemaObject],
                              headers: Seq[(String, SchemaObject)]
                              ) {
  def verify(context: SwaggerChecks, requestHeader: Map[String, String], requestBody: String): VerifyResult = {
    val bodyVerify = schema.map {
      expected =>
        expected.verify(context, Nil, CheckJsValue.parse(requestBody))
    }.getOrElse(VerifyResult.success)

    headers.foldLeft(bodyVerify) {
      case (result, (name, schema)) =>
        result.combine(
          requestHeader.get(name.toLowerCase)
            .map(value => schema.verify(context, Nil, CheckJsString.formatted(value)))
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