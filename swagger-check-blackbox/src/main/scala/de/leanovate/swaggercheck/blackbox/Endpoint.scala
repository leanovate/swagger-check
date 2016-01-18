package de.leanovate.swaggercheck.blackbox

import de.leanovate.swaggercheck.SwaggerChecks

class Endpoint(baseUrl: String)(implicit swaggerChecks: SwaggerChecks) {
  def path(path: String): Methods = {
    swaggerChecks.swaggerAPI.paths.get(path) match {
      case Some(operationsByMethod) => new Methods(baseUrl, operationsByMethod)
      case None => throw new RuntimeException(s"Unknown path: $path")
    }
  }
}
