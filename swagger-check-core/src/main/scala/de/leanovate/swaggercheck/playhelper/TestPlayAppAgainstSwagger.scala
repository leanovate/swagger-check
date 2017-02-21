package de.leanovate.swaggercheck.playhelper

import java.io.File

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.ValidationSuccess
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Properties}
import play.api.Application
import play.api.test.Helpers

/**
  * Generates many requests and tests the responses.
  *
  * Excerpt form example application:
  * {{{
  *   class ThingsControllerSpec("./ThingApi.yaml")({
  *     val mockThingsRepository = mock[ThingsRepository]
  *
  *     new GuiceApplicationBuilder()
  *       .overrides(bind[ThingsRepository].toInstance(mockThingsRepository))
  *       .build()
  *   })
  * }}}
  *
  * @param path path the swagger specification file (relative to the test runner)
  * @param app a play application probably with mocked databases and rest calls
  */
class TestPlayAppAgainstSwagger(path: String)(app: Application)
    extends Properties(path) {

  private def swaggerCheck = SwaggerChecks(new File(path))
  private implicit val arbitraryRequest = Arbitrary[PlayOperationVerifier](swaggerCheck.operationVerifier())

  property("app implements swagger specification") = forAll {
    requestVerifier: PlayOperationVerifier =>
      val Some(result) = Helpers.route(app, requestVerifier.request)

      requestVerifier.responseVerifier.verify(result) == ValidationSuccess
  }

}
