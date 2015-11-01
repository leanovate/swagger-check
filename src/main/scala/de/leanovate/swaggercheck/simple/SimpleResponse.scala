package de.leanovate.swaggercheck.simple

import de.leanovate.swaggercheck.ResponseExtractor

/**
 * Simple response object implementation.
 *
 * This is not supposed to be (or become) a web framework. Just a convenient fallback that
 * could be used without any extra dependencies.
 *
 * @param status the response status code (i.e. 200, 201, ...)
 * @param headers the response headers
 * @param body the response body
 */
case class SimpleResponse(
                           status: Int,
                           headers: Map[String, String],
                           body: String
                           )

object SimpleResponse {
  implicit val extractor = new ResponseExtractor[SimpleResponse] {
    override def status(response: SimpleResponse): Int = response.status

    override def headers(response: SimpleResponse): Map[String, String] =
      response.headers.map {
        case (name, value) =>
          name.toLowerCase -> value
      }

    override def body(response: SimpleResponse): String = response.body
  }
}