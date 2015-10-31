package de.leanovate.swaggercheck

import org.scalacheck.Prop
import org.scalacheck.Prop.Result
import scala.language.implicitConversions

/**
 * Result of a verification.
 */
sealed trait VerifyResult {
  /**
   * `true` if successful
   */
  def isSuccess: Boolean

  /**
   * Combine this result with another.
   *
   * Only successful if both are successful.
   *
   * @return combined result
   */
  def combine(result: VerifyResult): VerifyResult
}

object VerifyResult {
  /**
   * Create a verification success.
   */
  val success: VerifyResult = VerifySuccess

  /**
   * Create a verification error.
   *
   * @param failure error message
   */
  def error(failure: String): VerifyResult = VerifyError(Seq(failure))

  /**
   * Convert to a scala-check `Prop`.
   */
  implicit def verifyProp(verifyResult: VerifyResult): Prop = verifyResult match {
    case VerifySuccess => Prop.proved
    case VerifyError(failures) =>
      Prop(Result(status = Prop.False, labels = failures.toSet))
  }
}

case object VerifySuccess extends VerifyResult {
  override def isSuccess: Boolean = true

  override def combine(result: VerifyResult): VerifyResult = result
}

case class VerifyError(failures: Seq[String]) extends VerifyResult {
  override def isSuccess: Boolean = false

  override def combine(result: VerifyResult): VerifyResult = result match {
    case VerifySuccess => this
    case VerifyError(otherFailures) => VerifyError(failures ++ otherFailures)
  }
}
