package de.leanovate.swaggercheck.schema

import java.net.URLEncoder

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.leanovate.swaggercheck.model.CheckJsValue
import de.leanovate.swaggercheck.schema.Operation.RequestBuilder
import de.leanovate.swaggercheck.{RequestCreator, SwaggerChecks}
import org.scalacheck.Gen

import scala.collection.JavaConversions._

@JsonDeserialize(builder = classOf[OperationBuilder])
case class Operation(
                      consumes: Set[String],
                      produces: Set[String],
                      parameters: Seq[OperationParameter],
                      responses: Map[String, OperationResponse]
                      ) {

  def withDefaults(defaultConsumes: Set[String], defaultProduces: Set[String]): Operation =
    copy(consumes = consumes ++ defaultConsumes, produces = produces ++ defaultProduces)

  def generateRequest[R](context: SwaggerChecks, method: String, path: String)
                        (implicit requestCreator: RequestCreator[R]): Gen[R] = {
    val builder = parameters.foldLeft(RequestBuilder(method, path)) {
      (result, parameter) =>
        parameter.applyTo(context, result)
    }.withConsumes(consumes.toSeq).withProduces(produces.toSeq)

    builder.build()
  }
}

object Operation {

  case class RequestBuilder(method: String,
                            path: String,
                            pathParamGens: Seq[Gen[Option[(String, String)]]] = Seq.empty,
                            queryParamGens: Seq[Gen[Option[(String, String)]]] = Seq.empty,
                            headerGens: Seq[Gen[Option[(String, String)]]] = Seq.empty,
                            bodyGen: Gen[Option[CheckJsValue]] = Gen.const(None)) {

    def withConsumes(consumes: Seq[String]): RequestBuilder =
      if (consumes.isEmpty)
        this
      else
        copy(headerGens = headerGens :+ Gen.oneOf(consumes).map(c => Some("Content-Type" -> c)))

    def withProduces(produces: Seq[String]): RequestBuilder =
      if (produces.isEmpty)
        this
      else
        copy(headerGens = headerGens :+ Gen.oneOf(produces).map(p => Some("Accept" -> p)))

    def withPathParam(pathGen: Gen[Option[(String, String)]]): RequestBuilder =
      copy(pathParamGens = pathParamGens :+ pathGen)

    def withQueryParam(paramGen: Gen[Option[(String, String)]]): RequestBuilder =
      copy(queryParamGens = queryParamGens :+ paramGen)

    def withHeader(headerGen: Gen[Option[(String, String)]]): RequestBuilder =
      copy(headerGens = headerGens :+ headerGen)

    def withBody(body: Gen[CheckJsValue]): RequestBuilder =
      copy(bodyGen = body.map(Some.apply))

    def build[R]()(implicit requestCreator: RequestCreator[R]): Gen[R] = {
      for {
        pathParams <- Gen.sequence(pathParamGens)
        queryParams <- Gen.sequence(queryParamGens)
        headers <- Gen.sequence(headerGens)
        optBody <- bodyGen
      } yield {
        val queryString = queryParams.flatMap(_.toSeq).map {
          case (name, value) =>
            URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8")
        }.mkString("&")
        val pathWithParams = pathParams.foldLeft(path) {
          case (result, Some((name, value))) =>
            result.replace(s"{$name}", value)
          case (result, _) => result
        }
        val fullPath = if (queryString.isEmpty) pathWithParams else pathWithParams + "?" + queryString

        optBody match {
          case Some(body) => requestCreator.createJson(method.toUpperCase, fullPath, headers.flatMap(_.toSeq), body)
          case None => requestCreator.createEmpty(method.toUpperCase, fullPath, headers.flatMap(_.toSeq))
        }
      }
    }
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