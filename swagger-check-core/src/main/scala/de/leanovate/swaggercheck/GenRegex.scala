package de.leanovate.swaggercheck

import org.scalacheck.Gen

/**
 * Generator for regular expression.
 *
 * Admittedly the use-cases are somewhat limited, but I needed it here, so you may as well.
 */
object GenRegex {
  private def metacharacters = Set('.', '*', '-', '+', '?', '(', ')', '{', '}', '[', ']', '\\', '$', '^', '|', '/', '&')

  private def genAscii = Gen.choose(32: Char, 126: Char)

  private def genLiteral = genAscii.retryUntil(!metacharacters.contains(_))

  private def genOptionPart = Gen.oneOf(
    genLiteral,
    Gen.zip(Gen.alphaNumChar, Gen.alphaNumChar).map {
      case (from, to) if from < to => s"$from-$to"
      case (to, from) if from < to => s"$from-$to"
      case (from, _) => s"$from"
    }
  )

  private def genOption = Gen.nonEmptyListOf(genOptionPart).map(parts => s"[${parts.mkString}]")

  private def genTerm = Gen.oneOf(
    genLiteral,
    genOption
  )

  private def genRepetition = Gen.oneOf(
    Gen.const(""),
    Gen.const("+"),
    Gen.const("+"),
    Gen.const("?"),
    Gen.choose(1, 10).map { size => s"{$size}" },
    Gen.choose(1, 10).flatMap { size => Gen.choose(0, 10).map { min => s"{$min,${size + min}}" } }
  )

  def genExpr: Gen[String] = Gen.zip(genTerm, genRepetition).map {
    case (term, repetion) => s"$term$repetion"
  }
}
