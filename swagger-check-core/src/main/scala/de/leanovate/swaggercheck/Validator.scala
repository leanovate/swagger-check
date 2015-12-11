package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.schema.model.ValidationResult

/**
 * Generic trait to verify something.
 */
trait Validator[T] {
  /**
   * Verifies that a value matches a criteria.
   */
  def verify(value: T): ValidationResult[Unit]
}
