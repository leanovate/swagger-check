package de.leanovate.swaggercheck.schema.model.formats

import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}

trait ValueFormat[T] {
  def validate(path: JsonPath, node: T): ValidationResult
}
