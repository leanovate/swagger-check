package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.model.{CheckJsInteger, CheckJsValue}
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
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

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: CheckJsValue): VerifyResult = node match {
    case CheckJsInteger(_, _, value) =>
      if (minimum.exists(_ > value))
        VerifyResult.error(s"'$value' has to be greater than ${minimum.mkString}: ${path.mkString(".")}")
      else if (maximum.exists(_ < value))
        VerifyResult.error(s"'$value' has to be less than ${maximum.mkString}: ${path.mkString(".")}")
      else
        format.flatMap(ctx.integerFormats.get).map(_.verify(path.mkString("."), value)).getOrElse(VerifyResult.success)
    case _ =>
      VerifyResult.error(s"$node should be a long: $path")
  }
}
