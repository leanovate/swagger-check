package de.leanovate.swaggercheck.playhelper

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import de.leanovate.swaggercheck.RequestCreator
import de.leanovate.swaggercheck.model.CheckJsValue
import play.api.libs.json.{Json, JsValue}
import play.api.libs.json.jackson.PlayJsonModule
import play.api.mvc.AnyContent
import play.api.test.FakeRequest

object FakeRequests {
  implicit val creator = new RequestCreator[FakeRequest[_ <: AnyContent]] {
    override def createEmpty(method: String, uri: String, headers: Seq[(String, String)]): FakeRequest[_ <: AnyContent] =
      FakeRequest(method, uri).withHeaders(headers: _ *)

    override def createJson(method: String, uri: String, headers: Seq[(String, String)], body: CheckJsValue): FakeRequest[_ <: AnyContent] =
      FakeRequest(method, uri).withHeaders(headers: _*).withJsonBody(Json.parse(body.minified))
  }
}
