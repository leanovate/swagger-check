package de.leanovate.swaggercheck.playhelper

import org.scalatest.{MustMatchers, WordSpec}
import play.api.mvc.Results

import scala.concurrent.Future

class FutureResultsSpec extends WordSpec with MustMatchers {
  "FutureResults" should {
    "extract data from a play Future[Result]" in {
      val result = Results.Status(202)("{}").withHeaders("some" -> "header", "something" -> "else")
      val futureResult = Future.successful(result)

      FutureResults.responseExtractor.status(futureResult) mustBe 202
      FutureResults.responseExtractor.body(futureResult) mustBe "{}"
      FutureResults.responseExtractor.headers(futureResult) mustBe Map(
        "some" -> "header",
        "something" -> "else",
        "Content-Type" -> "text/plain; charset=utf-8")
    }
  }
}
