package de.leanovate.swaggercheck.model

/**
  * Json array.
  *
  * @param minSize optional minSize fro shrinking
  * @param elements elements of the array
  */
case class JsArray(
                    minSize: Option[Int],
                    elements: Seq[JsValue]
                  ) extends JsValue

object JsArray {
  /**
    * Get a fixed json array that will not shrink.
    */
  def fixed(elements: Seq[JsValue]) = JsArray(Some(elements.size), elements)
}