package de.leanovate.swaggercheck.schema

import java.net.URLEncoder

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.{RequestCreator, SwaggerChecks}
import de.leanovate.swaggercheck.schema.Operation.RequestBuilder
import org.scalacheck.Gen

import scala.collection.JavaConversions._

case class Operation(
                      consumes: Option[Seq[String]],
                      produces: Option[Seq[String]],
                      parameters: Option[Seq[OperationParameter]],
                      responses: Option[Map[String, OperationResponse]]
                      ) {
  def generateRequest[R](context: SwaggerChecks, method: String, path: String)(implicit requestCreator: RequestCreator[R]): Gen[R] = {
    val builder = parameters.toSeq.flatten.foldLeft(RequestBuilder(method, path)) {
      (result, parameter) =>
        parameter.applyTo(context, result)
    }

    builder.build()
  }
}

object Operation {

  case class RequestBuilder(method: String,
                            path: String,
                            pathParamGens: Seq[Gen[Option[(String, String)]]] = Seq.empty,
                            queryParamGens: Seq[Gen[Option[(String, String)]]] = Seq.empty,
                            headerGens: Seq[Gen[Option[(String, String)]]] = Seq.empty,
                            bodyGen: Gen[Option[JsonNode]] = Gen.const(None)) {

    def withPathParam(pathGen: Gen[Option[(String, String)]]): RequestBuilder =
      copy(pathParamGens = pathParamGens :+ pathGen)

    def withQueryParam(paramGen: Gen[Option[(String, String)]]): RequestBuilder =
      copy(queryParamGens = queryParamGens :+ paramGen)

    def withHeader(headerGen: Gen[Option[(String, String)]]): RequestBuilder =
      copy(headerGens = headerGens :+ headerGen)

    def withBody(body: Gen[JsonNode]): RequestBuilder =
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