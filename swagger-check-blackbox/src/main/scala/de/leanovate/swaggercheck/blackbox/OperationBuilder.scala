package de.leanovate.swaggercheck.blackbox

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.Operation
import org.scalacheck.Gen

class OperationBuilder(baseUrl: String, operation: Operation)(implicit swaggerChecks: SwaggerChecks) {
  def genRequest(): Gen[Request] = ???
}
