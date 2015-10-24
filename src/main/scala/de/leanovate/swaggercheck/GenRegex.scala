package de.leanovate.swaggercheck

import org.scalacheck.Gen

/**
 * Generator for regular expression.
 *
 * Admittedly the use-cases are somewhat limited, but I needed it here, so you may as well.
 */
object GenRegex {
  private def metacharacters = Set('.', '*', '-', '+', '?', '(', ')', '{', '}', '[', ']', '\\', '$', '^', '|')

  private def genAscii = Gen.choose(32: Char, 126: Char)

  private def genLiteral = genAscii.retryUntil(!metacharacters.contains(_))

  private def genOptionPart = Gen.oneOf(
    genLiteral,
    Gen.zip(genLiteral, genLiteral).map {
      case (from, to) if from < to => s"$from-$to"
      case (to, from) if from < to => s"$from-$to"
      case (from, _) => s"$from"
    }
  )

  private def genOption = Gen.nonEmptyListOf(genOptionPart).map(parts => s"[${parts.mkString}]")

  private def genSimpleTerm = Gen.oneOf(
    genLiteral,
    genOption
  )

  private def genTerm = Gen.oneOf(
    genSimpleTerm,
    genGroup
  )

  private def genRepetition = Gen.oneOf(
    Gen.const(""),
    Gen.const("+"),
    Gen.const("+"),
    Gen.const("?"),
    Gen.choose(1, 10).map { size => s"{$size}" },
    Gen.choose(1, 10).flatMap { size => Gen.choose(0, 10).map { min => s"{$min,${size + min}}" } }
  )

  private def genExpr: Gen[String] = Gen.zip(genTerm, genRepetition).map {
    case (term, repetion) => s"$term$repetion"
  }

  private def genGroup: Gen[String] = for {
    size <- Gen.choose(1, 3)
    groups <- Gen.listOfN(size, genSimpleTerm)
  } yield s"(${groups.mkString("|")})"

  /**
   * Creates a generator of (non-empty) regular expression.
   */
  def apply(): Gen[String] = Gen.listOfN(2, genExpr).map(_.mkString)

}
