// Loosely based on https://github.com/dcsobral/GenRegex
// Heavily modified (and fixed up) though

package de.leanovate.swaggercheck.generators

import org.scalacheck.Gen

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharSequenceReader

/**
  * Regex parser to generate a generator of matches.
  */
class GenRegexMatch extends Parsers {

  import GenRegexMatch._

  type Elem = Char
  type Atom = Parser[Gen[Char]]
  type Composition = Parser[Gen[List[Char]]]
  type Transformer = Parser[Gen[List[Char]] => Gen[List[Char]]]

  def regexGenerator(s: String): Gen[List[Char]] = regex.apply(new CharSequenceReader(s)) match {
    case Success(result, _) => result
    case errorMsg => throw new RuntimeException(errorMsg.toString)
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

  def regex: Composition = phrase(topLevelGroup)

  def repetitions: Transformer = (
    zeroOrOne ^^^ { term: Gen[List[Char]] => Gen.oneOf(term, Gen.const(Nil)) }
      | zeroOrMore ^^^ { term: Gen[List[Char]] => Gen.listOf(term).map(_.flatten) }
      | oneOrMore ^^^ { term: Gen[List[Char]] => Gen.nonEmptyListOf(term).map(_.flatten) }
      | startBoundedReps ~> number <~ endBoundedReps ^^ { size => term: Gen[List[Char]] => Gen.listOfN(size, term).map(_.flatten) }
      | startBoundedReps ~> number ~ ',' ~ number <~ endBoundedReps ^^ {
      case min ~ _ ~ max => term: Gen[List[Char]] => Gen.choose(min, max).flatMap(Gen.listOfN(_, term).map(_.flatten))
    }
    )

  def topLevelGroup: Composition = rep1sep(topLevelSequence, alternation) ^^ {
    case a :: b :: rest => Gen oneOf(a, b, rest: _*)
    case List(unique) => unique
  }

  def topLevelSequence: Composition =
    startSubexpr ~> elem('^') ~> sequence <~ opt(elem('$')) <~ endSubexpr |
      opt(elem('^')) ~> sequence <~ opt(elem('$'))

  def sequence: Composition = alt.* ^^ combine

  def alt: Composition = rep1sep(reps.+, alternation) ^^ {
    case a :: b :: rest => Gen oneOf(combine(a), combine(b), rest.map(combine): _*)
    case List(unique) => combine(unique)
  }

  def reps: Composition = term ~ repetitions.? ^^ {
    case term ~ Some(transform) => transform(term)
    case term ~ None => term
  }

  def term: Composition = simpleTerm | group

  def group: Composition = startSubexpr ~> sequence <~ endSubexpr

  def simpleTerm: Composition = (
    escaped ^^ { set => Gen.oneOf(set.toSeq) }
      | literal
      | wildcardMatch
      | negCharOptions
      | charOptions
    ) map (_ map (List(_)))

  def literal: Atom = literalChar ^^ Gen.const

  def escaped: Parser[Set[Char]] = escape ~> anyChar ^^ {
    case 'd' => digitSet
    case 'D' => nonDigitSet
    case 'w' => alphaNumSet
    case 'W' => nonAlphaNumSet
    case 's' => whiteSpaceSet
    case 'S' => nonWhiteSpaceSet
    case lit => Set(lit)
  }

  def negCharOptions: Atom = startCharClass ~> elem('^') ~ charOptionRange.+ <~ endCharClass ^^ {
    case _ ~ options =>
      val allOptions = options.flatten
      genWildcard.retryUntil(!allOptions.contains(_))
  }

  def charOptions: Atom = startCharClass ~> charOptionRange.+ <~ endCharClass ^^ { options =>
    Gen.oneOf(options.flatten.toSeq)
  }

  def wildcardMatch: Atom = wildcard ^^ { _ => genWildcard }

  def charOptionRange: Parser[Set[Char]] = (
    escaped
      | charRange
      | elem("In Range", _ != ']') ^^ (Set(_))
    )

  def charRange = literalChar ~ charRangeDelim ~ literalChar ^^ {
    case from ~ _ ~ to => rangeChar(from, to).toSet
  }

  def number: Parser[Int] = digit.+ ^^ (_.mkString.toInt)

  def combine(seq: List[Gen[List[Char]]]): Gen[List[Char]] =
    Gen.sequence[List[List[Char]], List[Char]](seq).map(_.flatten)
}

object GenRegexMatch {
  def rangeChar(from: Char, to: Char): TraversableOnce[Char] =
    Range(from.toInt, to.toInt).map(_.toChar)

  val anySet: Set[Char] = rangeChar(32: Char, 126: Char).toSet

  val digitSet: Set[Char] = anySet.filter(_.isDigit)

  val nonDigitSet: Set[Char] = anySet.filter(!_.isDigit)

  val alphaSet: Set[Char] = anySet.filter(_.isLetter)

  val alphaNumSet: Set[Char] = anySet.filter(ch => ch.isLetterOrDigit || ch == '_')

  val nonAlphaNumSet: Set[Char] = anySet.filter(ch => !ch.isLetterOrDigit && ch != '_')

  val whiteSpaceSet: Set[Char] = Set(' ', '\t')

  val nonWhiteSpaceSet: Set[Char] = anySet -(' ', '\t')

  val genWildcard = Gen choose(32: Char, 126: Char)
}