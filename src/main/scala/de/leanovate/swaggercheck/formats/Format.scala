package de.leanovate.swaggercheck.formats

import org.scalacheck.Gen

trait Format[T] {
  def generate: Gen[T]
}
