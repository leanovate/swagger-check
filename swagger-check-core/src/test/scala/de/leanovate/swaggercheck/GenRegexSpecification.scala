package de.leanovate.swaggercheck

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

import scala.util.Try

object GenRegexSpecification extends Properties("GenRegex") {
  property("can be compiled") = forAll(Generators.regex) {
    regex : String =>
      Try(regex.r).isSuccess
  }
}
