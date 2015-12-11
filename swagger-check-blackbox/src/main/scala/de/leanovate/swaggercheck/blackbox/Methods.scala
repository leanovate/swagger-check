package de.leanovate.swaggercheck.blackbox

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.Operation

class Methods(uri: String, operationsByMethod: Map[String, Operation])(implicit swaggerChecks: SwaggerChecks) {
  def method(method: String): OperationBuilder = operationsByMethod.get(method.toLowerCase) match {
    case Some(operation) => new OperationBuilder(uri, operation)
    case None => throw new RuntimeException(s"No operation for $method $uri")
  }

  def get = method("get")

  def post = method("post")

  def patch = method("patch")

  def put = method("put")

  def delete = method("delete")
}
