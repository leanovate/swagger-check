package de.leanovate.swaggercheck.schema

import java.net.URLEncoder

import de.leanovate.swaggercheck.RequestCreator
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Gen

import scala.collection.JavaConversions._

case class OperationRequestBuilder(method: String,
                                   path: String,
                                   pathParamGens: Seq[Gen[Option[(String, String)]]] = Seq.empty,
                                   queryParamGens: Seq[Gen[Option[(String, String)]]] = Seq.empty,
                                   headerGens: Seq[Gen[Option[(String, String)]]] = Seq.empty,
                                   bodyGen: Gen[Option[CheckJsValue]] = Gen.const(None)) {

  def withConsumes(consumes: Seq[String]): OperationRequestBuilder =
    if (consumes.isEmpty)
      this
    else
      copy(headerGens = headerGens :+ Gen.oneOf(consumes).map(c => Some("Content-Type" -> c)))

  def withProduces(produces: Seq[String]): OperationRequestBuilder =
    if (produces.isEmpty)
      this
    else
      copy(headerGens = headerGens :+ Gen.oneOf(produces).map(p => Some("Accept" -> p)))

  def withPathParam(pathGen: Gen[Option[(String, String)]]): OperationRequestBuilder =
    copy(pathParamGens = pathParamGens :+ pathGen)

  def withQueryParam(paramGen: Gen[Option[(String, String)]]): OperationRequestBuilder =
    copy(queryParamGens = queryParamGens :+ paramGen)

  def withHeader(headerGen: Gen[Option[(String, String)]]): OperationRequestBuilder =
    copy(headerGens = headerGens :+ headerGen)

  def withBody(body: Gen[CheckJsValue]): OperationRequestBuilder =
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
