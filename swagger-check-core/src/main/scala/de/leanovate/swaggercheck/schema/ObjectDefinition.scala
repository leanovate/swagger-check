package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.model.{JsonPath, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsNull, CheckJsObject, CheckJsValue}
import org.scalacheck.Gen

import scala.collection.JavaConversions._

case class ObjectDefinition(
                             required: Option[Set[String]],
                             properties: Option[Map[String, SchemaObject]],
                             additionalProperties: Option[SchemaObject]
                             ) extends SchemaObject {
  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[CheckJsValue] = {
    if (properties.isEmpty) {
      additionalProperties match {
        case Some(additionalSchema) =>
          for {
            size <- Gen.choose(0, ctx.maxItems)
            properties <- Gen.listOfN(size, Gen.zip(Gen.identifier, additionalSchema.generate(ctx.childContext)))
          } yield CheckJsObject(Set.empty, None, properties.toMap)
        case None =>
          arbitraryObj(ctx)
      }
    } else {
      properties.map {
        props =>
          val propertyGens: Traversable[Gen[(String, CheckJsValue)]] = {
            props.map {
              case (name, schema) if required.exists(_.contains(name)) =>
                schema.generate(ctx.childContext).map(value => name -> value)
              case (name, schema) =>
                Gen.oneOf(
                  Gen.const(CheckJsNull),
                  schema.generate(ctx.childContext)
                ).map(value => name -> value)
            }
          }
          Gen.sequence(propertyGens).map {
            fields =>
              CheckJsObject(required.getOrElse(Set.empty), None, fields.toMap)
          }
      }.getOrElse(arbitraryObj(ctx))
    }
  }

  override def verify(ctx: SwaggerChecks, path: JsonPath, node: CheckJsValue): ValidationResult = node match {
    case CheckJsObject(_, _, fields) =>
      properties.map {
        props =>
          props.foldLeft(ValidationResult.success) {
            case (result, (name, schema)) =>
              val field = fields.getOrElse(name, CheckJsNull)
              if (!field.isNull || required.exists(_.contains(name)))
                result.combine(schema.verify(ctx, path.field(name), field))
              else
                ValidationResult.success
          }
      }.getOrElse(ValidationResult.success)
    case _ =>
      ValidationResult.error(s"$node should be an object: ${path}")
  }
}
