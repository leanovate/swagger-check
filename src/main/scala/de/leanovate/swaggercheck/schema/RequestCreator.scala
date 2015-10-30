package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode

trait RequestCreator[R] {
  def createEmpty(method: String, path: String, headers: Seq[(String, String)]): R

  def createJson(method: String, path: String, headers: Seq[(String, String)], body: JsonNode): R
}
