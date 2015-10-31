package de.leanovate.swaggercheck

import com.fasterxml.jackson.databind.JsonNode

trait RequestCreator[R] {
  def createEmpty(method: String, uri: String, headers: Seq[(String, String)]): R

  def createJson(method: String, uri: String, headers: Seq[(String, String)], body: JsonNode): R
}
