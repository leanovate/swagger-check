package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

case class ArrayDefinition(
                            minItems: Option[Int],
                            maxItems: Option[Int],
                            items: Option[SchemaObject]
                            ) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = items.map {
    itemsSchema =>
      val min = minItems.getOrElse(0)
      val max = Math.min(min + ctx.maxItems, maxItems.getOrElse(min + ctx.maxItems))
      for {
        size <- Gen.choose(min, max)
        elements <- Gen.listOfN(size, itemsSchema.generate(ctx.childContext))
      } yield elements.foldLeft(nodeFactory.arrayNode()) {
        case (result, element) =>
          result.add(element)
      }
  }.getOrElse(arbitraryArray(ctx))

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    if (node.isArray) {
      if (minItems.exists(_ > node.size()))
        VerifyResult.error(s"$node should have at least ${minItems.mkString} items: ${path.mkString(".")}")
      else if (maxItems.exists(_ < node.size()))
        VerifyResult.error(s"$node should have at least ${maxItems.mkString} items: ${path.mkString(".")}")
      else
        items.map {
          itemsSchema =>
            Range(0, node.size()).foldLeft(VerifyResult.success) {
              (result, index) =>
                val element = node.get(index)
                result.combine(itemsSchema.verify(ctx, (path.mkString(".") + s"[$index]") :: Nil, element))
            }
        }.getOrElse(VerifyResult.success)
    } else {
      VerifyResult.error(s"${node.getNodeType} should be an array: ${path.mkString(".")}")
    }
  }
}
