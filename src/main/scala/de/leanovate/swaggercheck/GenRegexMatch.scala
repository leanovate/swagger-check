package de.leanovate.swaggercheck

import org.scalacheck.Gen

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharSequenceReader

class GenRegexMatch extends Parsers {
  type Elem = Char
  type Atom = Parser[Gen[Char]]
  type Composition = Parser[Gen[List[Char]]]

  def genWildcard = Gen choose(32: Char, 126: Char)

  def regexGenerator(s: String): Gen[List[Char]] = regex.apply(new CharSequenceReader(s)) match {
    case Success(result, _) => result
    case errorMsg => sys.error(errorMsg.toString)
  }

  def regex: Parser[Gen[List[Char]]] = sequence

  def metacharacters = (wildcard
    | alternation
    | repetitions
    | startBoundedReps | endBoundedReps
    | startCharClass | endCharClass
    | startSubexpr | endSubexpr
    )

  val wildcard = elem('.')
  val alternation = elem('|')

  def repetitions = elem(zeroOrOne) | elem(zeroOrMore) | elem(oneOrMore)

  val zeroOrOne = '?'
  val zeroOrMore = '*'
  val oneOrMore = '+'

  val any = elem("any", _ => true)
  val startBoundedReps = elem('{')
  val endBoundedReps = elem('}')
  val startCharClass = elem('[')
  val endCharClass = elem(']')
  val startSubexpr = elem('(')
  val endSubexpr = elem(')')
  val optionNeg = elem('^')
  val charRangeDelim = elem('-')
  val charOption = elem("Char option", ch => ch != '-' && ch != ']')
  val escape = elem('\\')

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

  def simpleTerm: Composition = (escaped | literal | wildcardMatch | negCharOptions | charOptions) map (_ map (List(_)))

  def literal: Atom = not(metacharacters) ~> any ^^ Gen.const

  def escaped: Atom = escape ~> any ^^ {
    case 'd' => Gen.numChar
    case 'w' => Gen.alphaNumChar
    case lit => Gen.const(lit)
  }

  def negCharOptions: Atom = startCharClass ~> elem('^') ~ charOptionRange.* <~ endCharClass ^^ {
    case _ ~ options =>
      val allOptions = options.flatten
      genWildcard.suchThat(!allOptions.contains(_))
  }

  def charOptions: Atom = startCharClass ~> charOptionRange.* <~ endCharClass ^^ { options =>
    Gen.oneOf(options.flatten)
  }

  def wildcardMatch: Atom = wildcard ^^ { _ => genWildcard }

  def charOptionRange: Parser[Seq[Char]] = (
    escape ~> any ^^ (Seq(_))
      | charRange
      | charOption ^^ (Seq(_))
    )

  def charRange = charOption ~ charRangeDelim ~ charOption ^^ {
    case from ~ _ ~ to => Range(from.toInt, to.toInt).map(_.toChar).toSeq
  }
}

object GenRegexMatch {
  def apply(regex: String): Gen[String] = new GenRegexMatch().regexGenerator(regex).map(_.mkString)
}