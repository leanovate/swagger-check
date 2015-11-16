package de.leanovate.swaggercheck.shrinkable

import com.fasterxml.jackson.core.JsonGenerator
import org.scalacheck.Shrink

import scala.collection.immutable.Stream._

/**
  * Json integer.
  *
  * @param min Optional minimum for shrinking
  * @param value the integer value
  */
case class CheckJsInteger(
                           min: Option[BigInt],
                           max: Option[BigInt],
                           value: BigInt
                         ) extends CheckJsValue {
  override def asText(default: String): String = value.toString

  override def generate(json: JsonGenerator): Unit = json.writeNumber(value.underlying())

  override def shrink: Stream[CheckJsInteger] = {
    def halfs(n: BigInt): Stream[BigInt] =
      if (n == BigInt(0) || min.exists(_ >= n) || max.exists(_ <= n))
        empty
      else
        cons(n, halfs(n / 2))

    if (value == BigInt(0) || min.exists(_ >= value) || max.exists(_ <= value))
      empty
    else {
      val ns = halfs(value / 2).map(value - _)
      val start = min.filter(_ > 0).orElse(max.filter(_ < 0)).getOrElse(BigInt(0))
      cons(start, ns).map(CheckJsInteger(min, max, _))
    }
  }
}

object CheckJsInteger {
  /**
    * Get a fixed json integer that will not shrink.
    */
  def fixed(value: BigInt) = CheckJsInteger(Some(value), Some(value), value)

  implicit lazy val shrinkJsValue: Shrink[CheckJsInteger] = Shrink[CheckJsInteger](_.shrink)
}