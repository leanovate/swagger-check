package de.leanovate.swaggercheck

/**
 * Extractor for response objects of the webframework of your choice.
 */
trait ResponseExtractor[R] {
  /**
   * Get the status code of the response.
   */
  def status(response: R): Int

  /**
   * Get the response headers.
   *
   * Header names are suppose to be extracted as lower case.
   */
  def headers(value: R): Map[String, String]

  /**
   * Get the response body.
   */
  def body(value: R): String
}
