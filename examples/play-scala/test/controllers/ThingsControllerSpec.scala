package controllers

import java.util.UUID

import dal.ThingsRepository
import de.leanovate.swaggercheck.VerifySuccess
import de.leanovate.swaggercheck.playhelper._
import models.Thing
import org.scalacheck.{Arbitrary, Gen}
import org.specs2.ScalaCheck
import org.specs2.mock.Mockito
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import support.{Arbitraries, ThingApi}

import scala.concurrent.Future

class ThingsControllerSpec extends PlaySpecification with ScalaCheck with ThingApi with Mockito with Arbitraries{

  "ThingController" should {
    "support all /things routes" in {
      implicit val arbitraryRequest = Arbitrary[PlayOperationVerifier](swaggerCheck.operationVerifier())

      prop {
        requestVerifier: PlayOperationVerifier =>
          val Some(result) = route(requestVerifier.request)

          status(result) must between(200, 300)

          requestVerifier.responseVerifier.verify(result) must be equalTo VerifySuccess
      }.setContext(new WithApplication(testApp()) {})
    }
  }

  def testApp(): Application = {
    val mockThingsRepository = mock[ThingsRepository]

    mockThingsRepository.getPage(any[Int], any[Int]) answers { _ => Future.successful(Gen.nonEmptyListOf(Arbitrary.arbitrary[Thing]).sample.getOrElse(Seq.empty)) }
    mockThingsRepository.getById(any[UUID]) answers { _ => Future.successful(Arbitrary.arbitrary[Thing].sample)}

    new GuiceApplicationBuilder()
      .overrides(bind[ThingsRepository].toInstance(mockThingsRepository))
      .build()
  }
}
