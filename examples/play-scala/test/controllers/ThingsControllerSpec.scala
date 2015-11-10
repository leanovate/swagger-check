package controllers

import dal.ThingsRepository
import org.scalacheck.Arbitrary
import org.specs2.ScalaCheck
import org.specs2.mock.Mockito
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContent
import play.api.test._
import support.ThingApi

class ThingsControllerSpec extends PlaySpecification with ScalaCheck with ThingApi with Mockito {

  import de.leanovate.swaggercheck.playhelper.FakeRequests._

  "ThingController" should {
    "bla" in {
      implicit val app = testApp()
      implicit val arbitraryRequest = Arbitrary[FakeRequest[String]](
        swaggerCheck.requestGenerator[FakeRequest[String]](None))

      prop {
        request: FakeRequest[String] =>
          Helpers.running(testApp()) {
            val Some(result) = route(request)

            status(result) must between(200, 300)
          }
      }
    }
  }

  def testApp(): Application = {
    val mockThingsRepository = mock[ThingsRepository]

    new GuiceApplicationBuilder()
      .overrides(bind[ThingsRepository].toInstance(mockThingsRepository))
      .build()
  }
}
