package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator
import org.scalacheck.Shrink

/**
  * An unformatted (arbitrary) json string.
  *
  * @param minLength optional minLength for shrinking
  * @param value the string value
  */
case class CheckJsUnformattedString(
                                     minLength: Option[Int],
                                     value: String
                                   ) extends CheckJsValue {
  override def generate(json: JsonGenerator): Unit = json.writeString(value)

  override def shrink: Stream[CheckJsUnformattedString] = minLength match {
    case Some(len) if value.length <= len => Stream.empty
    case Some(len) => Shrink.shrink(value).filter(_.length >= len).map(CheckJsUnformattedString(minLength, _))
    case None => Shrink.shrink(value).map(CheckJsUnformattedString(minLength, _))
  }
}

object CheckJsUnformattedString {
  implicit lazy val shrinkJsValue: Shrink[CheckJsUnformattedString] = Shrink[CheckJsUnformattedString](_.shrink)
}
