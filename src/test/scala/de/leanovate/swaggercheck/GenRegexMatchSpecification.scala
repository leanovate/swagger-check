package de.leanovate.swaggercheck

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object GenRegexMatchSpecification extends Properties("GenRegexMatch") {
  property("Any match") = checkRegex(".*")

  property("Email like match") = checkRegex("[a-zA-Z0-9\\.]+@[a-z]+\\.[a-z]+")

  property("UUID like match") = checkRegex("[0-9a-f]{8}(\\-[0-9a-f]{4}){3}\\-[0-9a-f]{12}")

  property("Any regex") = forAll(GenRegex()) {
    regex => checkRegex(regex)
  }

  def checkRegex(regex: String) = forAll(GenRegexMatch(regex)) {
    (str: String) =>
      val matches = regex.r.findFirstIn(str)

      matches.exists(_ == str)
  }
}
