package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.Operation.RequestBuilder
import org.scalacheck.Gen

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
        optBody match {
          case Some(body) => requestCreator.createJson(method, path, Seq.empty, body)
          case None => requestCreator.createEmpty(method, path, Seq.empty)
        }
      }
    }
  }

}