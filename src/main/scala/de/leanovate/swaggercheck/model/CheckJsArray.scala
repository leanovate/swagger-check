package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator
import org.scalacheck.Shrink

/**
  * Json array.
  *
  * @param minSize optional minSize fro shrinking
  * @param elements elements of the array
  */
case class CheckJsArray(
                         minSize: Option[Int],
                         elements: Seq[CheckJsValue]
                       ) extends CheckJsValue {
  override def generate(json: JsonGenerator): Unit = {
    json.writeStartArray()
    elements.foreach(_.generate(json))
    json.writeEndArray()
  }

  override def shrink: Stream[CheckJsArray] = minSize match {
    case Some(size) if elements.size <= size => Stream.empty
    case Some(size) => Shrink.shrink(elements).filter(_.size >= size).map(CheckJsArray(minSize, _))
    case None => Shrink.shrink(elements).map(CheckJsArray(minSize, _))
  }
}

object CheckJsArray {
  /**
    * Get a fixed json array that will not shrink.
    */
  def fixed(elements: Seq[CheckJsValue]) = CheckJsArray(Some(elements.size), elements)

  implicit lazy val shrinkJsValue: Shrink[CheckJsArray] = Shrink[CheckJsArray](_.shrink)
}