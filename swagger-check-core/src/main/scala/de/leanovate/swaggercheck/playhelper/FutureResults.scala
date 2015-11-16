package de.leanovate.swaggercheck.playhelper


import de.leanovate.swaggercheck.ResponseExtractor
import play.api.http.{HeaderNames, Status}
import play.api.mvc.Result
import play.api.test.{DefaultAwaitTimeout, ResultExtractors}

import scala.concurrent.Future

object FutureResults extends ResultExtractors with HeaderNames with Status with DefaultAwaitTimeout {
  implicit val responseExtractor = new ResponseExtractor[Future[Result]] {
    override def status(response: Future[Result]): Int = FutureResults.status(response)

    override def body(response: Future[Result]): String = FutureResults.contentAsString(response)

    override def headers(response: Future[Result]): Map[String, String] = FutureResults.headers(response)
  }
}
