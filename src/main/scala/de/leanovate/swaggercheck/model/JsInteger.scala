package de.leanovate.swaggercheck.model

import java.math.BigInteger

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
                      value: BigInt
                    ) extends JsValue {
  override def generate(json: JsonGenerator): Unit = json.writeNumber(value.underlying())

  override def shrink: Stream[JsInteger] = {
    def halfs(n: BigInt): Stream[BigInt] =
      if(n <= min.getOrElse(0))
        empty
      else
        cons(n, halfs(n/2))

    if(value <= min.getOrElse(0))
      empty
    else {
      val ns = halfs(value/2).map(value - _)
      cons(min.getOrElse(BigInt(0)), ns).map(JsInteger(min, _))
    }
  }
}

object JsInteger {
  /**
    * Get a fixed json integer that will not shrink.
    */
  def fixed(value: BigInt) = JsInteger(Some(value), value)

  implicit lazy val shrinkJsValue: Shrink[JsInteger] = Shrink[JsInteger](_.shrink)
}