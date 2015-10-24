package de.leanovate.swaggercheck

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.swagger.models.Model
import io.swagger.models.properties.Property

class SwaggerJsonVerifier(model: Model) {
  def verifyNode(node: ObjectNode, properties: Map[String, Property]): VerifyResult =
    properties.foldLeft(VerifyResult.success) {
      (result, property) =>
        result.combine(verifyProperty(Option(node.get(property._1)), property._2))
    }

  def verifyProperty(optValue: Option[JsonNode], property: Property): VerifyResult = ???
}
