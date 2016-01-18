package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.leanovate.swaggercheck.{RequestCreator, SwaggerChecks}
import org.scalacheck.Gen

@JsonDeserialize(builder = classOf[OperationBuilder])
case class Operation(
                      consumes: Set[String],
                      produces: Set[String],
                      parameters: Seq[OperationParameter],
                      responses: Map[String, OperationResponse],
                      method: String = "",
                      uri: String = ""
                    ) {

  def withDefaults(method: String, uri: String, defaultConsumes: Set[String], defaultProduces: Set[String]): Operation =
    copy(
      method = method,
      uri = uri,
      consumes = consumes ++ defaultConsumes,
      produces = produces ++ defaultProduces
    )

  def generateRequest[R](context: SwaggerChecks, method: String, path: String)
                        (implicit requestCreator: RequestCreator[R]): Gen[R] = {
    val builder = parameters.foldLeft(OperationRequestBuilder(method, path)) {
      (result, parameter) =>
        parameter.applyTo(context, result)
    }.withConsumes(consumes.toSeq).withProduces(produces.toSeq)

    builder.build()
  }
}

class OperationBuilder @JsonCreator()(
                                       @JsonProperty("consumes") consumes: Option[Seq[String]],
                                       @JsonProperty("produces") produces: Option[Seq[String]],
                                       @JsonProperty("parameters") parameters: Option[Seq[OperationParameter]],
                                       @JsonProperty("responses") responses: Option[Map[String, OperationResponse]]
                                     ) {
  def build(): Operation = Operation(
    consumes.map(_.toSet).getOrElse(Set.empty),
    produces.map(_.toSet).getOrElse(Set.empty),
    parameters.getOrElse(Seq.empty),
    responses.getOrElse(Map.empty))
}