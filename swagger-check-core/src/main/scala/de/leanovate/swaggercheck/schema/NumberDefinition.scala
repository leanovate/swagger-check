package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.model.{ CheckJsValue, CheckJsNumber }
import de.leanovate.swaggercheck.{ SwaggerChecks, VerifyResult }
import org.scalacheck.{ Arbitrary, Gen }
import de.leanovate.swaggercheck.model.CheckJsInteger

case class NumberDefinition(
    format: Option[String],
    minimum: Option[BigDecimal],
    maximum: Option[BigDecimal]) extends SchemaObject {

  override def generate(ctx: SwaggerChecks): Gen[CheckJsValue] = {
    val generator: Gen[BigDecimal] = format match {
      case Some(formatName) if ctx.numberFormats.contains(formatName) =>
        ctx.numberFormats(formatName).generate
      case _ => Arbitrary.arbitrary[BigDecimal]
    }
    generator.map(value => CheckJsNumber(minimum, maximum, value))
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: CheckJsValue): VerifyResult = node match {
    case CheckJsNumber(_, _, value) =>
      verifyValue(ctx, path, value)
    case CheckJsInteger(_, _, value) =>
      verifyValue(ctx, path, BigDecimal(value))
    case _ =>
      VerifyResult.error(s"${node} should be a long: $path")
  }

  private def verifyValue(ctx: SwaggerChecks, path: Seq[String], value: BigDecimal): VerifyResult = {
    if (minimum.exists(_ > value))
      VerifyResult.error(s"'$value' has to be greater than ${minimum.mkString}: ${path.mkString(".")}")
    else if (maximum.exists(_ < value))
      VerifyResult.error(s"'$value' has to be less than ${maximum.mkString}: ${path.mkString(".")}")
    else
      format.flatMap(ctx.numberFormats.get).map(_.verify(path.mkString("."), value)).getOrElse(VerifyResult.success)
  }
}
