package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.{VerifyResult, SwaggerChecks}
import org.scalacheck.Gen

@JsonTypeName("number")
case class NumberDefinition(
                             format: Option[String],
                             minimum: Option[Double],
                             maximum: Option[Double]
                             ) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = {
    val generator: Gen[Double] = format match {
      case Some(formatName) if ctx.numberFormats.contains(formatName) =>
        ctx.numberFormats(formatName).generate
      case _ => Gen.choose(minimum.getOrElse(0), maximum.getOrElse(Double.MaxValue))
    }
    generator.map(nodeFactory.numberNode)
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    if (node.isNumber) {
      val value = node.asDouble()
      if (minimum.exists(_ > value))
        VerifyResult.error(s"'$value' has to be greater than ${minimum.mkString}: ${path.mkString(".")}")
      else if (maximum.exists(_ < value))
        VerifyResult.error(s"'$value' has to be less than ${maximum.mkString}: ${path.mkString(".")}")
      else
        format.flatMap(ctx.numberFormats.get).map(_.verify(path.mkString("."), value)).getOrElse(VerifyResult.success)
    } else {
      VerifyResult.error(s"${node.getNodeType} should be a long: $path")
    }
  }
}
