package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator
import org.scalacheck.Shrink

/**
  * An unformatted (arbitrary) json string.
  *
  * @param minLength optional minLength for shrinking
  * @param value the string value
  */
case class JsUnformattedString(
                                minLength: Option[Int],
                                value: String
                              ) extends JsValue {
  override def generate(json: JsonGenerator): Unit = json.writeString(value)

  override def shrink: Stream[JsUnformattedString] = minLength match {
    case Some(len) if value.length <= len => Stream.empty
    case Some(len) => Shrink.shrink(value).filter(_.length >= len).map(JsUnformattedString(minLength, _))
    case None => Shrink.shrink(value).map(JsUnformattedString(minLength, _))
  }
}

object JsUnformattedString {
  implicit lazy val shrinkJsValue: Shrink[JsUnformattedString] = Shrink[JsUnformattedString](_.shrink)
}
