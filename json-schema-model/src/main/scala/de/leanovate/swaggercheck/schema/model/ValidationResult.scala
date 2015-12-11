package de.leanovate.swaggercheck.schema.model

/**
 * Result of a verification.
 */
sealed trait ValidationResult[+T] {
  /**
    * Result of the validation (if there is one)
    */
  def result : T

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
  def combine[U >: T](other: ValidationResult[U]): ValidationResult[T]

  def map[U](f : T => U) : ValidationResult[U]

  def flatMap[U](f: T => ValidationResult[U]) : ValidationResult[U]
}

object ValidationResult {
  /**
   * Create a verification success.
   */
  def success[T](result : T): ValidationResult[T] = ValidationSuccess[T](result)

  /**
   * Create a verification error.
   *
   * @param failure error message
   */
  def error[T](failure: String): ValidationResult[T] = ValidationFailure[T](Seq(failure))
}

case class ValidationSuccess[T](result : T) extends ValidationResult[T] {
  override def isSuccess: Boolean = true

  override def combine[U >: T](other: ValidationResult[U]): ValidationResult[T] = other match {
    case ValidationSuccess(_) => this
    case ValidationFailure(failures) => ValidationFailure[T](failures)
  }

  override def map[U](f: (T) => U): ValidationResult[U] = ValidationSuccess[U](f(result))

  override def flatMap[U](f: (T) => ValidationResult[U]): ValidationResult[U] = f(result)
}

case class ValidationFailure[T](failures: Seq[String]) extends ValidationResult[T] {
  override def result: T = throw new RuntimeException(s"Validation has failed: ${failures.mkString(", ")}")

  override def isSuccess: Boolean = false

  override def combine[U >: T](other: ValidationResult[U]): ValidationResult[T] = other match {
    case ValidationSuccess(_) => this
    case ValidationFailure(otherFailures) => ValidationFailure[T](failures ++ otherFailures)
  }

  override def map[U](f: (T) => U): ValidationResult[U] = ValidationFailure[U](failures)

  override def flatMap[U](f: (T) => ValidationResult[U]): ValidationResult[U] = ValidationFailure[U](failures)
}
