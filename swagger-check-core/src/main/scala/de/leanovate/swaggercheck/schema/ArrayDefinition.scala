package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsArray, CheckJsValue}
import org.scalacheck.Gen

case class ArrayDefinition(
                            minItems: Option[Int],
                            maxItems: Option[Int],
                            items: Option[SchemaObject]
                          ) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[CheckJsValue] = items.map {
    itemsSchema =>
      val min = minItems.getOrElse(0)
      val max = Math.min(min + ctx.maxItems, maxItems.getOrElse(min + ctx.maxItems))
      for {
        size <- Gen.choose(min, max)
        elements <- Gen.listOfN(size, itemsSchema.generate(ctx.childContext))
      } yield CheckJsArray(minItems, elements)
  }.getOrElse(arbitraryArray(ctx))

  override def verify(ctx: SwaggerChecks, path: JsonPath, node: CheckJsValue): ValidationResult = node match {
    case CheckJsArray(_, elements) =>
      if (minItems.exists(_ > elements.size))
        ValidationResult.error(s"$node should have at least ${minItems.mkString} items: ${path}")
      else if (maxItems.exists(_ < elements.size))
        ValidationResult.error(s"$node should have at least ${maxItems.mkString} items: ${path}")
      else
        items.map {
          itemsSchema =>
            elements.zipWithIndex.foldLeft(ValidationResult.success) {
              case (result, (element, index)) =>
                result.combine(itemsSchema.verify(ctx, path.index(index), element))
            }
        }.getOrElse(ValidationResult.success)
    case _ =>
      ValidationResult.error(s"$node should be an array: ${path}")
  }
}
