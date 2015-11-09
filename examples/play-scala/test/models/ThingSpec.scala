package models

import de.leanovate.swaggercheck.VerifySuccess
import de.leanovate.swaggercheck.model.CheckJsValue
import org.scalacheck.Arbitrary
import org.specs2.ScalaCheck
import org.specs2.matcher.MustMatchers
import org.specs2.mutable.Specification
import play.api.libs.json.{JsSuccess, Json}

class ThingSpec extends Specification with ScalaCheck with MustMatchers with Arbitraries with ThingApi {
  "Thing" should {
    "be receivable" in {
      implicit val arbitraryJson = Arbitrary[CheckJsValue](swaggerCheck.jsonGenerator("Thing"))

      prop {
        json: CheckJsValue =>
          val JsSuccess(thing, path) = Json.parse(json.minified).validate[Thing]

          path.toString() must be equalTo ""
      }
    }

    "be sendable" in {
      val verifier = swaggerCheck.jsonVerifier("Thing")

      prop {
        thing: Thing =>
          verifier.verify(Json.stringify(Json.toJson(thing))) must be equalTo VerifySuccess
      }
    }
  }
}
