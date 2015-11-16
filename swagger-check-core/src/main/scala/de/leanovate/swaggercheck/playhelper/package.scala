package de.leanovate.swaggercheck

import play.api.mvc.Result
import play.api.test.FakeRequest

import scala.concurrent.Future

package object playhelper {
  implicit val requestCreator: RequestCreator[FakeRequest[String]] = FakeRequests.creator

  implicit val responseExtractor: ResponseExtractor[Future[Result]] = FutureResults.responseExtractor

  type PlayOperationVerifier = OperationValidator[FakeRequest[String], Future[Result]]
}
