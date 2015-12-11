package de.leanovate.swaggercheck.blackbox

import de.leanovate.swaggercheck.SwaggerChecks

object BlackboxSwaggerChecks {
  def endpoint(url: String)(implicit swaggerChecks: SwaggerChecks) = new Endpoint(url)

  def endpoint(host: String, port: Int)(implicit swaggerChecks: SwaggerChecks): Endpoint = endpoint(s"http://$host:$port")
}
