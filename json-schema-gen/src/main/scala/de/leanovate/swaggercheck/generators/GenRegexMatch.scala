// Loosely based on https://github.com/dcsobral/GenRegex
// Heavily modified (and fixed up) though

package de.leanovate.swaggercheck.generators

import org.scalacheck.Gen

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharSequenceReader

/**
  * Regex parser to generate a generator of matches.
  */
object GenRegexMatch {

  def apply(s: String): Gen[List[Char]] =
    GenRegexParser(new CharSequenceReader(s))

  private object GenRegexParser extends Parsers {

    def apply(s: CharSequenceReader): Gen[List[Char]] = GenRegexParser.regex(s) match {
      case Success(result, _) => result
      case error: NoSuccess => throw new RuntimeException(error.msg)
    }

    type Elem = Char
    type Atom = Parser[Gen[Char]]
    type Composition = Parser[Gen[List[Char]]]
    type Transformer = Parser[Gen[List[Char]] => Gen[List[Char]]]

    val metacharacters = Set('.', '*', '-', '+', '?', '(', ')', '{', '}', '[',
      ']', '\\', '$', '^', '|')

    val wildcard: Parser[Char] = elem('.')
    val alternation: Parser[Char] = elem('|')

    val zeroOrOne: Parser[Char] = elem('?')
    val zeroOrMore: Parser[Char] = elem('*')
    val oneOrMore: Parser[Char] = elem('+')

    val startBoundedReps: Parser[Char] = elem('{')
    val endBoundedReps: Parser[Char] = elem('}')
    val startCharClass: Parser[Char] = elem('[')
    val endCharClass: Parser[Char] = elem(']')
    val startSubexpr: Parser[Char] = elem('(')
    val endSubexpr: Parser[Char] = elem(')')
    val optionNeg: Parser[Char] = elem('^')
    val charRangeDelim: Parser[Char] = elem('-')
    val escape: Parser[Char] = elem('\\')
    val literalChar: Parser[Char] =
      elem("Literal char", !metacharacters.contains(_))
    val anyChar: Parser[Char] = elem("Any char", _ => true)
    val digit: Parser[Char] = elem("Digit", _.isDigit)

    def regex: Composition = phrase(topLevelGroup)

    def repetitions: Transformer = (
      zeroOrOne ^^^ { term: Gen[List[Char]] =>
        Gen.oneOf(term, Gen.const(Nil))
      }
        | zeroOrMore ^^^ { term: Gen[List[Char]] =>
          Gen.listOf(term).map(_.flatten)
        }
        | oneOrMore ^^^ { term: Gen[List[Char]] =>
          Gen.nonEmptyListOf(term).map(_.flatten)
        }
        | startBoundedReps ~> number <~ endBoundedReps ^^ {
          size => term: Gen[List[Char]] =>
            Gen.listOfN(size, term).map(_.flatten)
        }
        | startBoundedReps ~> number ~ ',' ~ number <~ endBoundedReps ^^ {
          case min ~ _ ~ max =>
            term: Gen[List[Char]] =>
              Gen.choose(min, max).flatMap(Gen.listOfN(_, term).map(_.flatten))
        }
    )

    def topLevelGroup: Composition =
      rep1sep(topLevelSequence, alternation) ^^ {
        case a :: b :: rest => Gen.oneOf(a, b, rest: _*)
        case List(unique) => unique
      }

    def topLevelSequence: Composition =
      startSubexpr ~> elem('^') ~> sequence <~ opt(elem('$')) <~ endSubexpr |
        opt(elem('^')) ~> sequence <~ opt(elem('$'))

    def sequence: Composition = alt.* ^^ combine

    def alt: Composition = rep1sep(reps.+, alternation) ^^ {
      case a :: b :: rest =>
        Gen.oneOf(combine(a), combine(b), rest.map(combine): _*)
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
      Gen.oneOf(options.flatten)
    }

    def wildcardMatch: Atom = wildcard ^^ { _ => genWildcard }

    def charOptionRange: Parser[Set[Char]] = (
      escaped
        | charRange
        | elem("In Range", _ != ']') ^^ (Set(_))
    )

    def charRange: Parser[Set[Char]] =
      (literalChar <~ charRangeDelim) ~ literalChar ^^ {
        case from ~ to => rangeChar(from, to).toSet
      }

    def number: Parser[Int] = digit.+ ^^ (_.mkString.toInt)

    def combine(seq: List[Gen[List[Char]]]): Gen[List[Char]] =
      Gen.sequence[List[List[Char]], List[Char]](seq).map(_.flatten)

    def rangeChar(from: Char, to: Char): TraversableOnce[Char] =
      Range(from.toInt, to.toInt).map(_.toChar)

    val anySet: Set[Char] = rangeChar(32: Char, 126: Char).toSet

    val digitSet: Set[Char] = anySet.filter(_.isDigit)

    val nonDigitSet: Set[Char] = anySet.filter(!_.isDigit)

    val alphaSet: Set[Char] = anySet.filter(_.isLetter)

    val alphaNumSet: Set[Char] =
      anySet.filter(ch => ch.isLetterOrDigit || ch == '_')

    val nonAlphaNumSet: Set[Char] =
      anySet.filter(ch => !ch.isLetterOrDigit && ch != '_')

    val whiteSpaceSet: Set[Char] = Set(' ', '\t')

    val nonWhiteSpaceSet: Set[Char] = anySet - (' ', '\t')

    val genWildcard: Gen[Char] = Gen choose (32: Char, 126: Char)
  }

}
