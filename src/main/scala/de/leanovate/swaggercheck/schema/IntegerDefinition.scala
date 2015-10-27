package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.{VerifyResult, SwaggerChecks}
import org.scalacheck.Gen


@JsonTypeName("integer")
case class IntegerDefinition(
                              format: Option[String],
                              minimum: Option[Long],
                              maximum: Option[Long]
                              ) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = {
    val generator: Gen[Long] = format match {
      case Some(formatName) if ctx.integerFormats.contains(formatName) =>
        ctx.integerFormats(formatName).generate
      case _ => Gen.choose(minimum.getOrElse(Long.MinValue), maximum.getOrElse(Long.MaxValue))
    }
    generator.map(nodeFactory.numberNode)
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    if (node.isNumber && node.canConvertToLong) {
      val value = node.asLong()
      if (minimum.exists(_ > value))
        VerifyResult.error(s"'$value' has to be greater than ${minimum.mkString}: ${path.mkString(".")}")
      else if (maximum.exists(_ < value))
        VerifyResult.error(s"'$value' has to be less than ${maximum.mkString}: ${path.mkString(".")}")
      else
        format.flatMap(ctx.integerFormats.get).map(_.verify(path.mkString("."), value)).getOrElse(VerifyResult.success)
    } else {
      VerifyResult.error(s"${node.getNodeType} should be a long: $path")
    }
  }
}
