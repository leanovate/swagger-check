package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator
import org.scalacheck.Shrink

/**
  * An json string.
  *
  * @param formatted `true` if the string is formatted according to some rules (and shall not be shrinked)
  * @param minLength optional minLength for shrinking
  * @param value the string value
  */
case class CheckJsString(
                          formatted: Boolean,
                          minLength: Option[Int],
                          value: String
                        ) extends CheckJsValue {
  override def asText(default: String): String = value

  override def generate(json: JsonGenerator): Unit = json.writeString(value)

  override def shrink: Stream[CheckJsString] =
    if (formatted)
      Stream.empty
    else
      minLength match {
        case Some(len) if value.length <= len => Stream.empty
        case Some(len) => Shrink.shrink(value).filter(_.length >= len).map(CheckJsString(formatted, minLength, _))
        case None => Shrink.shrink(value).map(CheckJsString(formatted, minLength, _))
      }
}

object CheckJsString {
  def unformatted(value:String) = CheckJsString(formatted = false, None, value)
  def formatted(value: String) = CheckJsString(formatted = true, None, value)

  implicit lazy val shrinkJsValue: Shrink[CheckJsString] = Shrink[CheckJsString](_.shrink)
}
