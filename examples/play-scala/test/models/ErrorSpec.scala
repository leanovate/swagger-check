package models

import de.leanovate.swaggercheck.schema.model.ValidationResult
import org.specs2.ScalaCheck
import org.specs2.matcher.MustMatchers
import org.specs2.mutable.Specification
import play.api.libs.json.Json
import support.{ThingApi, Arbitraries}

class ErrorSpec extends Specification with ScalaCheck with MustMatchers with Arbitraries with ThingApi {
  "Error" should {
    "be sendable" in {
      val verifier = swaggerCheck.jsonVerifier("Error")

      prop {
        error: Error =>
          verifier.verify(Json.stringify(Json.toJson(error))) must be equalTo ValidationResult.success
      }
    }
  }
}
