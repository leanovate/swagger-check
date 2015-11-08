package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.model.CheckJsValue

/**
  * Creator for request object for the webframework of your choice.
  */
trait RequestCreator[R] {
  /**
    * Create a request without body.
    *
    * @param method the request method (GET, POST, ...)
    * @param uri the request uri
    * @param headers thr request headers
    */
  def createEmpty(method: String, uri: String, headers: Seq[(String, String)]): R

  /**
    * Create a request with json body.
    *
    * @param method the request method (GET, POST, ...)
    * @param uri the request uri
    * @param headers thr request headers
    * @param body the request body
    */
  def createJson(method: String, uri: String, headers: Seq[(String, String)], body: CheckJsValue): R
}
