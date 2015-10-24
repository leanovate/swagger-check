package de.leanovate.swaggercheck

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object GenRegexMatchSpecification extends Properties("GenRegexMatch") {
  property("Any match") = checkRegex(".*")

  property("Email like match") = checkRegex("[a-zA-Z0-9\\.]+@[a-z]+\\.[a-z]+")

  property("UUID like match") = checkRegex("[0-9a-f]{8}(\\-[0-9a-f]{4}){3}\\-[0-9a-f]{12}")

  property("URL like match") = checkRegex("(https?|ftp)://[^\\s/$\\.?#].[^\\s]*")

  property("Strange 1") = checkRegex("[1-v5P-d sv-wO-jdLaEIG-a4-duK4-fj-rt-yh1-s;M8EV-rE-w,:\\&\\&]+[oR2];?")

  property("Any regex") = forAll(Generators.regex) {
    regex =>
      checkRegex(regex)
  }

  def checkRegex(regex: String) = forAll(Generators.regexMatch(regex)) {
    (str: String) =>
      val matches = regex.r.findFirstIn(str)

      matches.exists(_ == str)
  }
}
