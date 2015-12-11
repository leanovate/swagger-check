package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.schema.model.{ValidationSuccess, ValidationFailure, ValidationResult}
import org.scalacheck.Prop
import org.scalacheck.Prop.Result

import scala.language.implicitConversions

object ValidationResultToProp {
  /**
    * Convert to a scala-check `Prop`.
    */
  implicit def verifyProp(verifyResult: ValidationResult[_]): Prop = verifyResult match {
    case ValidationSuccess(_) => Prop.proved
    case ValidationFailure(failures) => Prop(Result(status = Prop.False, labels = failures.toSet))
  }
}
