package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsInteger, CheckJsNumber, CheckJsValue}
import org.scalacheck.{Arbitrary, Gen}

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

  override def verify(ctx: SwaggerChecks, path: JsonPath, node: CheckJsValue): ValidationResult = node match {
    case CheckJsNumber(_, _, value) =>
      verifyValue(ctx, path, value)
    case CheckJsInteger(_, _, value) =>
      verifyValue(ctx, path, BigDecimal(value))
    case _ =>
      ValidationResult.error(s"${node} should be a long: $path")
  }

  private def verifyValue(ctx: SwaggerChecks, path: JsonPath, value: BigDecimal): ValidationResult = {
    if (minimum.exists(_ > value))
      ValidationResult.error(s"'$value' has to be greater than ${minimum.mkString}: ${path}")
    else if (maximum.exists(_ < value))
      ValidationResult.error(s"'$value' has to be less than ${maximum.mkString}: ${path}")
    else
      format.flatMap(ctx.numberFormats.get).map(_.validate(path, value)).getOrElse(ValidationResult.success)
  }
}
