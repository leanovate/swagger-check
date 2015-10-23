// Loosely based on https://github.com/dcsobral/GenRegex
// Heavily modified (and fixed up) though

package de.leanovate.swaggercheck

import org.scalacheck.Gen

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharSequenceReader

/**
 * Regex parser to generate a generator of matches.
 */
class GenRegexMatch extends Parsers {
  type Elem = Char
  type Atom = Parser[Gen[Char]]
  type Composition = Parser[Gen[List[Char]]]
  type Transformer = Parser[Gen[List[Char]] => Gen[List[Char]]]

  def genWildcard = Gen choose(32: Char, 126: Char)

  def regexGenerator(s: String): Gen[List[Char]] = regex.apply(new CharSequenceReader(s)) match {
    case Success(result, _) => result
    case errorMsg => sys.error(errorMsg.toString)
  }

  val metacharacters = Set('.', '*', '-', '+', '?', '(', ')', '{', '}', '[', ']', '\\', '$', '^', '|')

  val wildcard = elem('.')
  val alternation = elem('|')
  
  val zeroOrOne = elem('?')
  val zeroOrMore = elem('*')
  val oneOrMore = elem('+')

  val startBoundedReps = elem('{')
  val endBoundedReps = elem('}')
  val startCharClass = elem('[')
  val endCharClass = elem(']')
  val startSubexpr = elem('(')
  val endSubexpr = elem(')')
  val optionNeg = elem('^')
  val charRangeDelim = elem('-')
  val escape = elem('\\')
  val literalChar = elem("Literal char", !metacharacters.contains(_))
  val anyChar = elem("Any char", _ => true)
  val digit = elem("Digit", _.isDigit)

  def regex: Composition = sequence

  def repetitions: Transformer = (
    zeroOrOne ^^^ { term: Gen[List[Char]] => Gen.oneOf(term, Gen.const(Nil)) }
      | zeroOrMore ^^^ { term: Gen[List[Char]] => Gen.listOf(term).map(_.flatten) }
      | oneOrMore ^^^ { term: Gen[List[Char]] => Gen.nonEmptyListOf(term).map(_.flatten) }
      | startBoundedReps ~> number <~ endBoundedReps ^^ { size => term: Gen[List[Char]] => Gen.listOfN(size, term).map(_.flatten) }
      | startBoundedReps ~> number ~ ',' ~ number <~ endBoundedReps ^^ {
      case min ~ _ ~ max => term: Gen[List[Char]] => Gen.choose(min, max).flatMap(Gen.listOfN(_, term).map(_.flatten))
    }
    )

  def sequence: Composition = alt.* ^^ {
    Gen.sequence[List[List[Char]], List[Char]](_).map(_.flatten)
  }

  def alt: Composition = rep1sep(reps, alternation) ^^ {
    case a :: b :: rest => Gen oneOf(a, b, rest: _*)
    case List(unique) => unique
  }

  def reps: Composition = term ~ repetitions.? ^^ {
    case term ~ Some(transform) => transform(term)
    case term ~ None => term
  }

  def term: Composition = simpleTerm | group

  def group: Composition = startSubexpr ~> sequence <~ endSubexpr

  def simpleTerm: Composition = (escaped | literal | wildcardMatch | negCharOptions | charOptions) map (_ map (List(_)))

  def literal: Atom = literalChar ^^ Gen.const

  def escaped: Atom = escape ~> anyChar ^^ {
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
    escape ~> anyChar ^^ (Seq(_))
      | charRange
      | literalChar ^^ (Seq(_))
    )

  def charRange = literalChar ~ charRangeDelim ~ literalChar ^^ {
    case from ~ _ ~ to => Range(from.toInt, to.toInt).map(_.toChar).toSeq
  }

  def number: Parser[Int] = digit.+ ^^ (_.mkString.toInt)
}

object GenRegexMatch {
  /**
   * Create a string generator for matches to a regex.
   *
   * The outcome is the same a string generator with `suchThat` containing a regex match.
   * We just find more positive this way (far far more positives, dependeing on the actualy regex)
   *
   * @param regex the regex that has to be matched
   * @return generator of matches
   */
  def apply(regex: String): Gen[String] = new GenRegexMatch().regexGenerator(regex).map(_.mkString)
}