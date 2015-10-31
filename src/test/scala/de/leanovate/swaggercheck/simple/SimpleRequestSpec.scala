package de.leanovate.swaggercheck.simple

import org.scalatest.{FlatSpec, MustMatchers}

class SimpleRequestSpec extends FlatSpec with MustMatchers {
  "SimpleRequest" should "decode query params" in {
    val request = SimpleRequest.create("GET", "/the/path?param1=value2&param2=value2", Seq.empty, None)

    request.path mustEqual "/the/path"
    request.queryParameters must have size 2
  }
}
