package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.{Generators, SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

case class StringDefinition(
                             format: Option[String],
                             minLength: Option[Int],
                             maxLength: Option[Int],
                             pattern: Option[String],
                             enum: Option[List[String]]
                             ) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = {
    val generator: Gen[String] = (enum, pattern, format) match {
      case (Some(one :: Nil), _, _) => Gen.const(one)
      case (Some(first :: second :: rest), _, _) => Gen.oneOf(first, second, rest: _ *)
      case (_, Some(regex), _) => Generators.regexMatch(regex)
      case (_, _, Some(formatName)) if ctx.stringFormats.contains(formatName) =>
        ctx.stringFormats(formatName).generate
      case _ => for {
        len <- Gen.choose(minLength.getOrElse(0), maxLength.getOrElse(255))
        chars <- Gen.listOfN(len, Gen.alphaNumChar)
      } yield chars.mkString
    }
    generator.map(nodeFactory.textNode)
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    if (node.isTextual) {
      val value = node.asText()
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
    } else {
      VerifyResult.error(s"${node.getNodeType} should be a string: ${path.mkString(".")}")
    }
  }
}
