package de.leanovate.swaggercheck.blackbox

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.Operation

class Methods(baseUrl: String, operationsByMethod: Map[String, Operation])(implicit swaggerChecks: SwaggerChecks) {
  def method(method: String): OperationBuilder = operationsByMethod.get(method.toUpperCase) match {
    case Some(operation) => new OperationBuilder(baseUrl, operation)
    case None => throw new RuntimeException(s"No operation for $method $baseUrl")
  }

  def GET = method("GET")

  def POST = method("POST")

  def PATCH = method("PATCH")

  def PUT = method("PUT")

  def DELETE = method("DELETE")
}
