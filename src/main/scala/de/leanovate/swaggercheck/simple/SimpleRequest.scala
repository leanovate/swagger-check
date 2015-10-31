package de.leanovate.swaggercheck.simple

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.RequestCreator

case class SimpleRequest(
                          method: String,
                          path: String,
                          queryParameters: Seq[(String, String)],
                          headers: Seq[(String, String)],
                          body: Option[JsonNode]
                          )

object SimpleRequest {
  def create(method: String, uri: String, headers: Seq[(String, String)], body: Option[JsonNode]): SimpleRequest = {
    val parts = uri.split("\\?")

    val path = parts.head

    val queryParameters = (if (parts.length > 1) parts(1) else "").split("&").map(_.split("=").toList).flatMap {
      case name :: value :: Nil =>
        Seq(name -> value)
      case _ =>
        Seq.empty
    }

    SimpleRequest(method, path, queryParameters, headers, body)
  }

  implicit val creator = new RequestCreator[SimpleRequest] {
    override def createEmpty(method: String, uri: String, headers: Seq[(String, String)]): SimpleRequest =
      create(method, uri, headers, None)

    override def createJson(method: String, uri: String, headers: Seq[(String, String)], body: JsonNode): SimpleRequest =
      create(method, uri, headers, Some(body))

  }
}