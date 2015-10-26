package de.leanovate.swaggercheck

import org.scalacheck.Prop
import org.scalacheck.Prop.Result
import scala.language.implicitConversions

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

  implicit def verifyProp(verifyResult: VerifyResult): Prop = verifyResult match {
    case VerifySuccess => Prop.proved
    case VerifyError(failures) => Prop(Result(status = Prop.Exception(new RuntimeException(failures.mkString("\n")))))
  }
}