package de.leanovate.swaggercheck

sealed trait VerifyResult {
  def isSuccess: Boolean

  def combine(result: VerifyResult): VerifyResult
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

object VerifyResult {
  val success: VerifyResult = VerifySuccess

  def error(failure: String): VerifyResult = VerifyError(Seq(failure))
}