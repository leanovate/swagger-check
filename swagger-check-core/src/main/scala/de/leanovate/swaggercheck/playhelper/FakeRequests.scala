package de.leanovate.swaggercheck.playhelper

import de.leanovate.swaggercheck.RequestCreator
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import play.api.test.FakeRequest

object FakeRequests {
  implicit val creator = new RequestCreator[FakeRequest[String]] {
    override def createEmpty(method: String, uri: String, headers: Seq[(String, String)]): FakeRequest[String] =
      FakeRequest(method, uri).withHeaders(headers: _ *).withBody("")

    override def createJson(method: String, uri: String, headers: Seq[(String, String)], body: CheckJsValue): FakeRequest[String] =
      FakeRequest(method, uri).withHeaders(headers: _*).withBody(body.minified)
  }
}
