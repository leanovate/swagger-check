package support

import java.io.File

import de.leanovate.swaggercheck.SwaggerChecks

trait ThingApi {
  val swaggerCheck = SwaggerChecks(new File("./ThingApi.yaml"))

}
