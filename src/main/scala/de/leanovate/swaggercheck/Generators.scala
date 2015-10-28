package de.leanovate.swaggercheck

import org.scalacheck.Gen

object Generators {
  def regex: Gen[String] =
    Gen.listOfN(3, GenRegex.genExpr).map(_.mkString).suchThat(!_.isEmpty)

  def regexMatch(regex: String): Gen[String] =
    new GenRegexMatch().regexGenerator(regex).map(_.mkString).suchThat(!_.isEmpty)

  def email: Gen[String] = for {
    name <- Gen.listOfN(20, Gen.alphaLowerChar).map(_.mkString)
    domain <- Gen.listOfN(20, Gen.alphaLowerChar).map(_.mkString)
    tld <- Gen.listOfN(3, Gen.alphaLowerChar).map(_.mkString)
  } yield s"$name@$domain.$tld"

  def url: Gen[String] = for {
    schema <- Gen.oneOf("http", "https", "ftp")
    host <- Gen.identifier
    port <- Gen.choose(80, 1024)
    segmentCount <- Gen.choose(0, 10)
    segments <- Gen.listOfN(segmentCount, Gen.identifier)
  } yield s"$schema://$host:$port/${segments.mkString("/")}"

  def uri: Gen[String] = for {
    schema <- Gen.listOfN(5, Gen.alphaChar).map(_.mkString)
    host <- Gen.identifier
    port <- Gen.choose(80, 1024)
    segmentCount <- Gen.choose(0, 10)
    segments <- Gen.listOfN(segmentCount, Gen.identifier)
  } yield s"$schema://$host:$port/${segments.mkString("/")}"
}
