package de.leanovate.swaggercheck.simple

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.RequestCreator

case class SimpleRequest(
                          method: String,
                          uri: String,
                          headers: Seq[(String, String)],
                          body: Option[JsonNode]
                          )

object SimpleRequest {
  implicit val creator = new RequestCreator[SimpleRequest] {
    override def createEmpty(method: String, uri: String, headers: Seq[(String, String)]): SimpleRequest =
      SimpleRequest(method, uri, headers, None)

    override def createJson(method: String, uri: String, headers: Seq[(String, String)], body: JsonNode): SimpleRequest =
      SimpleRequest(method, uri, headers, Some(body))
  }
}