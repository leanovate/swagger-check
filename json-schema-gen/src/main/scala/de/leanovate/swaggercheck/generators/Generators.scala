package de.leanovate.swaggercheck.generators

import org.scalacheck.Gen

/**
 * Collection of generators that might come handy.
 */
object Generators {
  /**
   * Generator for regular expressions.
   * (Variants are restricted as it is extremely simple to create regular expression with
   * runaway backtraces)
   */
  def regex: Gen[String] =
    Gen.listOfN(3, GenRegex.genExpr).map(_.mkString).suchThat(!_.isEmpty)

  /**
   * Generate for matches of a regular expression.
   * @param regex the regular expression each generated string has to satisfy
   */
  def regexMatch(regex: String): Gen[String] =
    new GenRegexMatch().regexGenerator(regex).map(_.mkString).suchThat(!_.isEmpty)

  /**
   * Generate simple email addresses.
   * (Just covers the simple cases. Complexer email addresses reguire additional work)
   */
  def email: Gen[String] = for {
    name <- Gen.listOfN(20, Gen.alphaLowerChar).map(_.mkString)
    domain <- Gen.listOfN(20, Gen.alphaLowerChar).map(_.mkString)
    tld <- Gen.listOfN(3, Gen.alphaLowerChar).map(_.mkString)
  } yield s"$name@$domain.$tld"

  /**
   * Generate an url.
   */
  def url: Gen[String] = for {
    schema <- Gen.oneOf("http", "https", "ftp")
    host <- Gen.identifier
    port <- Gen.choose(80, 1024)
    segmentCount <- Gen.choose(0, 10)
    segments <- Gen.listOfN(segmentCount, Gen.identifier)
  } yield s"$schema://$host:$port/${segments.mkString("/")}"

  /**
   * Generate an uri.
   */
  def uri: Gen[String] = for {
    schema <- Gen.listOfN(5, Gen.alphaChar).map(_.mkString)
    host <- Gen.identifier
    port <- Gen.choose(80, 1024)
    segmentCount <- Gen.choose(0, 10)
    segments <- Gen.listOfN(segmentCount, Gen.identifier)
  } yield s"$schema://$host:$port/${segments.mkString("/")}"
}
