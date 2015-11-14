package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.generators.Generators
import de.leanovate.swaggercheck.shrinkable.{CheckJsString, CheckJsValue}
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

case class StringDefinition(
                             format: Option[String],
                             minLength: Option[Int],
                             maxLength: Option[Int],
                             pattern: Option[String],
                             enum: Option[List[String]]
                           ) extends SchemaObject {

  override def generate(ctx: SwaggerChecks): Gen[CheckJsValue] = {
    (enum, pattern, format) match {
      case (Some(one :: Nil), _, _) => Gen.const(CheckJsString.formatted(one))
      case (Some(first :: second :: rest), _, _) => Gen.oneOf(first, second, rest: _ *).map(CheckJsString.formatted)
      case (_, Some(regex), _) => Generators.regexMatch(regex).map(CheckJsString.formatted)
      case (_, _, Some(formatName)) if ctx.stringFormats.contains(formatName) =>
        ctx.stringFormats(formatName).generate.map(CheckJsString.formatted)
      case _ => for {
        len <- Gen.choose(minLength.getOrElse(0), maxLength.getOrElse(255))
        chars <- Gen.listOfN(len, Gen.alphaNumChar)
      } yield CheckJsString(formatted = false, minLength, chars.mkString)
    }
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: CheckJsValue): VerifyResult = node match {
    case CheckJsString(_, _, value) =>
      if (minLength.exists(_ > value.length))
        VerifyResult.error(s"'$value' has to be at least ${minLength.mkString} chars long: ${path.mkString(".")}")
      else if (maxLength.exists(_ < value.length))
        VerifyResult.error(s"'$value' has to be at most ${maxLength.mkString} chars long: ${path.mkString(".")}")
      else if (pattern.exists(!_.r.pattern.matcher(value).matches()))
        VerifyResult.error(s"'$value' has match '${pattern.mkString}': ${path.mkString(".")}")
      else if (enum.exists(e => e.nonEmpty && !e.contains(value)))
        VerifyResult.error(s"'$value' has to be one of ${enum.map(_.mkString(", ")).mkString}: ${path.mkString(".")}")
      else
        format.flatMap(ctx.stringFormats.get).map(_.verify(path.mkString("."), value)).getOrElse(VerifyResult.success)
    case _ =>
      VerifyResult.error(s"$node should be a string: ${path.mkString(".")}")
  }
}
