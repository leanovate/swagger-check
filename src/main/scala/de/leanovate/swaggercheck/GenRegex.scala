package de.leanovate.swaggercheck

import org.scalacheck.Gen

import scala.util.parsing.combinator.RegexParsers

class GenRegex extends RegexParsers {
  override def skipWhitespace = false

  type Atom = Parser[Gen[Char]]
  type Composition = Parser[Gen[List[Char]]]

  def genWildcard = Gen choose(32: Char, 126: Char)

  def regexGenerator(s: String): Gen[List[Char]] = parseAll(regex, s) match {
    case Success(result, _) => result
    case errorMsg => sys.error(errorMsg.toString)
  }

  def regex = sequence

  def metacharacters = (wildcard
    | alternation
    | repetitions
    | startBoundedReps | endBoundedReps
    | startCharClass | endCharClass
    | startSubexpr | endSubexpr
    )

  val wildcard = "."
  val alternation = "|"

  def repetitions = zeroOrOne | zeroOrMore | oneOrMore

  val zeroOrOne = "?"
  val zeroOrMore = "*"
  val oneOrMore = "+"
  val startBoundedReps = "{"
  val endBoundedReps = "}"
  val startCharClass = "["
  val endCharClass = "]"
  val startSubexpr = "("
  val endSubexpr = ")"

  def sequence: Composition = alt.* ^^ {
    Gen.sequence[List[List[Char]], List[Char]](_).map(_.flatten)
  }

  def alt: Composition = rep1sep(reps, alternation) ^^ {
    case a :: b :: rest => Gen oneOf(a, b, rest: _*)
    case List(unique) => unique
  }

  def reps: Composition = term ~ repetitions.? ^^ {
    case term ~ Some(`zeroOrOne`) =>
      for {
        size <- Gen choose(0, 1)
        list <- Gen listOfN(size, term)
      } yield list.flatten
    case term ~ Some(`zeroOrMore`) => Gen listOf term map (_.flatten)
    case term ~ Some(`oneOrMore`) => Gen nonEmptyListOf term map (_.flatten)
    case term ~ None => term
  }

  def term: Composition = simpleTerm | group

  def group: Composition = startSubexpr ~> sequence <~ endSubexpr

  def simpleTerm: Composition = (literal | escaped | wildcardMatch | charOptions) map (_ map (List(_)))

  def literal: Atom = not(metacharacters) ~> ".".r ^^ {
    _ charAt 0
  }

  def escaped: Atom = "\\" ~> ".".r ^^ {
    case "d" => Gen.numChar
    case "w" => Gen.alphaNumChar
    case lit => lit charAt 0
  }

  def charOptions: Atom = startCharClass ~> """[^\]]*""".r <~ endCharClass ^^ { options =>
    if (options startsWith "^") genWildcard suchThat (!options.tail.contains(_))
    else Gen oneOf options.toSeq
  }

  def wildcardMatch: Atom = wildcard ^^ { _ => genWildcard }
}
