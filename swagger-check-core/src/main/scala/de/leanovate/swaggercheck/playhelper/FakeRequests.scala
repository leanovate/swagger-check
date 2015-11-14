package de.leanovate.swaggercheck.playhelper

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import de.leanovate.swaggercheck.RequestCreator
import de.leanovate.swaggercheck.model.CheckJsValue
import play.api.libs.json.{Json, JsValue}
import play.api.libs.json.jackson.PlayJsonModule
import play.api.mvc.AnyContent
import play.api.test.FakeRequest

object FakeRequests {
  implicit val creator = new RequestCreator[FakeRequest[String]] {
    override def createEmpty(method: String, uri: String, headers: Seq[(String, String)]): FakeRequest[String] =
      FakeRequest(method, uri).withHeaders(headers: _ *).withBody("")

    override def createJson(method: String, uri: String, headers: Seq[(String, String)], body: CheckJsValue): FakeRequest[String] =
      FakeRequest(method, uri).withHeaders(headers: _*).withBody(body.minified)
  }
}
