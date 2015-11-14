package de.leanovate.swaggercheck

import org.scalacheck.Prop.forAllNoShrink
import org.scalacheck.Properties

object GenRegexMatchSpecification extends Properties("GenRegexMatch") {
  property("Any match") = checkRegex(".*")

  property("Email like match") = checkRegex("[a-zA-Z0-9\\.]+@[a-z]+\\.[a-z]+")

  property("Strict email match") = checkRegex("^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$")

  property("UUID like match") = checkRegex("[0-9a-f]{8}(\\-[0-9a-f]{4}){3}\\-[0-9a-f]{12}")

  property("URL like match") = checkRegex("(https?|ftp)://[^\\s/$\\.?#].[^\\s]*")

  property("Escapes") = checkRegex("\\d\\D\\s\\S\\w\\W")

  property("Strange 1") = checkRegex("[1-v5P-d sv-wO-jdLaEIG-a4-duK4-fj-rt-yh1-s;M8EV-rE-w,:\\&\\&]+[oR2];?")

  property("Any regex") = forAllNoShrink(Generators.regex) {
    regex =>
      checkRegex(regex)
  }

  def checkRegex(regex: String) = forAllNoShrink(Generators.regexMatch(regex)) {
    (str: String) =>
      val matches = regex.r.findFirstIn(str)

      matches.contains(str)
  }
}
