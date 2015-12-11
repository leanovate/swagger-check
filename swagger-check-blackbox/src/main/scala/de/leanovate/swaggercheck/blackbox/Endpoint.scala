package de.leanovate.swaggercheck.blackbox

import de.leanovate.swaggercheck.SwaggerChecks

class Endpoint(url: String)(implicit swaggerChecks: SwaggerChecks) {
  def path(path: String): Methods = {
    swaggerChecks.swaggerAPI.paths.get(path) match {
      case Some(operationsByMethod) =>
        new Methods(url + swaggerChecks.swaggerAPI.basePath, operationsByMethod)
      case None => throw new RuntimeException(s"Unknown path: $path")
    }
  }
}
