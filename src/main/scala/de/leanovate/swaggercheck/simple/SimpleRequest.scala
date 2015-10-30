package de.leanovate.swaggercheck.simple

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.RequestCreator

case class SimpleRequest(
                          method: String,
                          path: String,
                          headers: Seq[(String, String)],
                          body: Option[JsonNode]
                          )

object SimpleRequest {
  implicit val creator = new RequestCreator[SimpleRequest] {
    override def createEmpty(method: String, path: String, headers: Seq[(String, String)]): SimpleRequest =
      SimpleRequest(method, path, headers, None)

    override def createJson(method: String, path: String, headers: Seq[(String, String)], body: JsonNode): SimpleRequest =
      SimpleRequest(method, path, headers, Some(body))
  }
}