package de.leanovate.swaggercheck.generators

import java.net.{URI, URL}

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

import scala.util.Try

object GeneratorsSpecification extends Properties("Generators") {
  property("generate valid urls") = forAll(Generators.url) {
    url =>
      Try(new URL(url)).isSuccess
  }

  property("generate valid uris") = forAll(Generators.uri) {
    url =>
      Try(new URI(url)).isSuccess
  }
}
