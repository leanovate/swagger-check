package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator
import org.scalacheck.Shrink

/**
  * Json array.
  *
  * @param minSize optional minSize fro shrinking
  * @param elements elements of the array
  */
case class JsArray(
                    minSize: Option[Int],
                    elements: Seq[JsValue]
                  ) extends JsValue {
  override def generate(json: JsonGenerator): Unit = {
    json.writeStartArray()
    elements.foreach(_.generate(json))
    json.writeEndArray()
  }

  override def shrink: Stream[JsArray] = minSize match {
    case Some(size) if elements.size <= size => Stream.empty
    case Some(size) => Shrink.shrink(elements).filter(_.size >= size).map(JsArray(minSize, _))
    case None => Shrink.shrink(elements).map(JsArray(minSize, _))
  }
}

object JsArray {
  /**
    * Get a fixed json array that will not shrink.
    */
  def fixed(elements: Seq[JsValue]) = JsArray(Some(elements.size), elements)

  implicit lazy val shrinkJsValue: Shrink[JsArray] = Shrink[JsArray](_.shrink)
}