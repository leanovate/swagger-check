package de.leanovate.swaggercheck.playhelper

import de.leanovate.swaggercheck.playhelper
import de.leanovate.swaggercheck.shrinkable.CheckJsObject
import org.scalatest.{MustMatchers, WordSpec}

class FakeRequestsSpec extends WordSpec with MustMatchers {
  "FakeRequests" should {
    "create an empty FakeRequest" in {
      val request = playhelper.requestCreator.createEmpty("GET", "/the/uri", Seq("header1" -> "value1", "header2" -> "value2"))

      request.method mustBe "GET"
      request.uri mustBe "/the/uri"
      request.headers.get("header1") mustBe Some("value1")
      request.headers.get("header2") mustBe Some("value2")
    }

    "create a FakeRequest with body" in {
      val request = playhelper.requestCreator.createJson("POST", "/the/uri", Seq("header1" -> "value1", "header2" -> "value2"), CheckJsObject.empty)

      request.method mustBe "POST"
      request.uri mustBe "/the/uri"
      request.headers.get("header1") mustBe Some("value1")
      request.headers.get("header2") mustBe Some("value2")
      request.body mustBe "{}"
    }
  }
}
