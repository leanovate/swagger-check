package models

import de.leanovate.swaggercheck.VerifySuccess
import org.specs2.ScalaCheck
import org.specs2.matcher.MustMatchers
import org.specs2.mutable.Specification
import play.api.libs.json.Json
import support.{ThingApi, Arbitraries}

class ThingsPageSpec extends Specification with ScalaCheck with MustMatchers with Arbitraries with ThingApi {
  "ThingsPage" should {
    // ThingsPage is never send to the server, so we just check writes here
    "be sendable" in {
      val verifier = swaggerCheck.jsonVerifier("ThingsPage")

      prop {
        thing: ThingsPage =>
          verifier.verify(Json.stringify(Json.toJson(thing))) must be equalTo VerifySuccess
      }
    }
  }
}
