package de.leanovate.swaggercheck.shrinkable

import de.leanovate.swaggercheck.schema.model.ValidationResult

import scala.collection.generic
import scala.language.higherKinds

trait CheckJsReads[A] {
  def reads(json: CheckJsValue): ValidationResult[A]
}

object CheckJsReads {

  implicit object StringReads extends CheckJsReads[String] {
    override def reads(json: CheckJsValue): ValidationResult[String] = json match {
      case CheckJsString(_, _, value) => ValidationResult.success(value)
      case _ => ValidationResult.error("Expected string")
    }
  }

  implicit object BooleanReads extends CheckJsReads[Boolean] {
    override def reads(json: CheckJsValue): ValidationResult[Boolean] = json match {
      case CheckJsBoolean(value) => ValidationResult.success(value)
      case _ => ValidationResult.error("Expected boolean")
    }
  }

  implicit def traversableReads[F[_], A](implicit bf: generic.CanBuildFrom[F[_], A, F[A]], ra: CheckJsReads[A]) = new CheckJsReads[F[A]] {
    override def reads(json: CheckJsValue): ValidationResult[F[A]] = json match {
      case CheckJsArray(_, elements) =>
        val builder = bf()
        builder.sizeHint(elements)
        elements.foldLeft(ValidationResult.success(builder)) {
          (result, element) =>
            result.flatMap(b =>
              ra.reads(element).map(value => b += value)
            )
        }.map(_.result())
    }
  }

  def fromJson[T](o: T)(json: CheckJsValue)(implicit fjs: CheckJsReads[T]): ValidationResult[T] = fjs.reads(json)
}