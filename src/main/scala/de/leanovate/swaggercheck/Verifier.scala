package de.leanovate.swaggercheck

trait Verifier[T] {
  def verify(value: T): VerifyResult
}
