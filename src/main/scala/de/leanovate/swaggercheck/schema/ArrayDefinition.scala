package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.{VerifyResult, SwaggerChecks}
import org.scalacheck.Gen


@JsonTypeName("array")
case class ArrayDefinition(
                            minItems: Option[Int],
                            maxItems: Option[Int],
                            items: Option[SchemaObject]
                            ) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = items.map {
    itemsSchema =>
      for {
        size <- Gen.choose(minItems.getOrElse(0), maxItems.getOrElse(10))
        elements <- Gen.listOfN(size, itemsSchema.generate(ctx))
      } yield elements.foldLeft(nodeFactory.arrayNode()) {
        case (result, element) =>
          result.add(element)
      }
  }.getOrElse(arbitraryArray)

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
