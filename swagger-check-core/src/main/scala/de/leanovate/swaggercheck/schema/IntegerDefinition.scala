package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsInteger, CheckJsValue}
import org.scalacheck.{Arbitrary, Gen}

case class IntegerDefinition(
                              format: Option[String],
                              minimum: Option[BigInt],
                              maximum: Option[BigInt]
                              ) extends SchemaObject {

  override def generate(ctx: SwaggerChecks): Gen[CheckJsValue] = {
    val generator: Gen[BigInt] = format match {
      case Some(formatName) if ctx.integerFormats.contains(formatName) =>
        ctx.integerFormats(formatName).generate
      case _ => Arbitrary.arbitrary[BigInt]
    }
    generator.map(value => CheckJsInteger(minimum, maximum, value))
  }

  override def verify(ctx: SwaggerChecks, path: JsonPath, node: CheckJsValue): ValidationResult = node match {
    case CheckJsInteger(_, _, value) =>
      if (minimum.exists(_ > value))
        ValidationResult.error(s"'$value' has to be greater than ${minimum.mkString}: ${path}")
      else if (maximum.exists(_ < value))
        ValidationResult.error(s"'$value' has to be less than ${maximum.mkString}: ${path}")
      else
        format.flatMap(ctx.integerFormats.get).map(_.validate(path, value)).getOrElse(ValidationResult.success)
    case _ =>
      ValidationResult.error(s"$node should be a long: $path")
  }
}
