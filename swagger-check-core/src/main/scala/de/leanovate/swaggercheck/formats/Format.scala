package de.leanovate.swaggercheck.formats

import de.leanovate.swaggercheck.schema.model.ValidationResult
import org.scalacheck.Gen

trait Format[T] {
  def generate: Gen[T]

  def verify(path:String, value: T): ValidationResult
}
