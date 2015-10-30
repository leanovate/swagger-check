package de.leanovate.swaggercheck.playhelper

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import de.leanovate.swaggercheck.RequestCreator
import play.api.libs.json.JsValue
import play.api.libs.json.jackson.PlayJsonModule
import play.api.mvc.AnyContent
import play.api.test.FakeRequest

object FakeRequests {
  val mapper = (new ObjectMapper).registerModule(PlayJsonModule)

  implicit val creator = new RequestCreator[FakeRequest[_ <: AnyContent]] {
    override def createEmpty(method: String, path: String, headers: Seq[(String, String)]): FakeRequest[_ <: AnyContent] =
      FakeRequest(method, path).withHeaders(headers: _ *)

    override def createJson(method: String, path: String, headers: Seq[(String, String)], body: JsonNode): FakeRequest[_ <: AnyContent] =
      FakeRequest(method, path).withHeaders(headers: _*).withJsonBody(mapper.treeToValue(body, classOf[JsValue]))
  }
}
