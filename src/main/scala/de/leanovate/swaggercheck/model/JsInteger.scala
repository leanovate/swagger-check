package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.JsonGenerator
import org.scalacheck.Shrink

import scala.collection.immutable.Stream._

/**
  * Json integer.
  *
  * @param min Optional minimum for shrinking
  * @param value the integer value
  */
case class JsInteger(
                      min: Option[BigInt],
                      max: Option[BigInt],
                      value: BigInt
                    ) extends JsValue {
  override def generate(json: JsonGenerator): Unit = json.writeNumber(value.underlying())

  override def shrink: Stream[JsInteger] = {
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
      cons(start, ns).map(JsInteger(min, max, _))
    }
  }
}

object JsInteger {
  /**
    * Get a fixed json integer that will not shrink.
    */
  def fixed(value: BigInt) = JsInteger(Some(value), Some(value), value)

  implicit lazy val shrinkJsValue: Shrink[JsInteger] = Shrink[JsInteger](_.shrink)
}