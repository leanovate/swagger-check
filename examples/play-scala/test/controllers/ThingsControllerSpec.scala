package controllers

import dal.ThingsRepository
import models.Thing
import org.scalacheck.{Gen, Arbitrary}
import org.specs2.ScalaCheck
import org.specs2.mock.Mockito
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import support.{Arbitraries, ThingApi}

import scala.concurrent.Future

class ThingsControllerSpec extends PlaySpecification with ScalaCheck with ThingApi with Mockito with Arbitraries {

  import de.leanovate.swaggercheck.playhelper.FakeRequests._

  "ThingController" should {
    "bla" in {
      implicit val arbitraryRequest = Arbitrary[FakeRequest[String]](
        swaggerCheck.requestGenerator[FakeRequest[String]]())

      prop {
        request: FakeRequest[String] =>
            val Some(result) = route(request)

            status(result) must between(200, 300)
      }.setContext(new WithApplication(testApp()) {})
    }
  }

  def testApp(): Application = {
    val mockThingsRepository = mock[ThingsRepository]

    mockThingsRepository.getPage(any[Int], any[Int]) answers { _ => Future.successful(Gen.nonEmptyListOf(Arbitrary.arbitrary[Thing]).sample.getOrElse(Seq.empty))  }

    new GuiceApplicationBuilder()
      .overrides(bind[ThingsRepository].toInstance(mockThingsRepository))
      .build()
  }
}
