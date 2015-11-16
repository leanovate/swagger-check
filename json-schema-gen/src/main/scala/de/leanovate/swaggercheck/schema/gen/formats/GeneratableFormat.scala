package de.leanovate.swaggercheck.schema.gen.formats

import de.leanovate.swaggercheck.schema.model.formats.ValueFormat
import org.scalacheck.Gen

trait GeneratableFormat[T] extends ValueFormat[T] {
  def generate: Gen[T]
}
