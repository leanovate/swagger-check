package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{Definition, JsonPath, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsString, CheckJsValue}

@JsonDeserialize(builder = classOf[OperationResponseBuilder])
case class OperationResponse(
                              schema: Option[Definition],
                              headers: Seq[(String, Definition)]
                              ) {
  def verify(context: SwaggerChecks, requestHeader: Map[String, String], requestBody: String): ValidationResult = {
    val bodyVerify = schema.map {
      expected =>
        expected.validate(context, JsonPath(), CheckJsValue.parse(requestBody))
    }.getOrElse(ValidationResult.success)

    headers.foldLeft(bodyVerify) {
      case (result, (name, schema)) =>
        result.combine(
          requestHeader.get(name.toLowerCase)
            .map(value => schema.validate[CheckJsValue](context, JsonPath(), CheckJsString.formatted(value)))
            .getOrElse(ValidationResult.success))
    }
  }
}

class OperationResponseBuilder @JsonCreator()(
                                               @JsonProperty("schema") schema: Option[Definition],
                                               @JsonProperty("headers") headers: Option[Map[String, Definition]]
                                               ) {
  def build(): OperationResponse = OperationResponse(schema, headers.map(_.toSeq).getOrElse(Seq.empty))
}