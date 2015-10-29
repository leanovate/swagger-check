package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

import scala.collection.JavaConversions._

case class ObjectDefinition(
                             required: Option[Set[String]],
                             properties: Option[Map[String, SchemaObject]],
                             additionalProperties: Option[SchemaObject]
                             ) extends SchemaObject {
  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = {
    if (properties.isEmpty) {
      additionalProperties match {
        case Some(additionalSchema) =>
          for {
            size <- Gen.choose(0, 10)
            properties <- Gen.listOfN(size, Gen.zip(Gen.identifier, additionalSchema.generate(ctx)))
          } yield properties.foldLeft(nodeFactory.objectNode()) {
            (result, prop) =>
              result.set(prop._1, prop._2)
              result
          }
        case None =>
          arbitraryObj
      }
    } else {
      properties.map {
        props =>
          val propertyGens: Traversable[Gen[(String, JsonNode)]] = {
            props.map {
              case (name, schema) if required.exists(_.contains(name)) =>
                schema.generate(ctx).map(value => name -> value)
              case (name, schema) =>
                Gen.oneOf(
                  Gen.const(nodeFactory.nullNode()),
                  schema.generate(ctx)
                ).map(value => name -> value)
            }
          }
          Gen.sequence(propertyGens).map(_.foldLeft(nodeFactory.objectNode()) {
            (result, element) =>
              result.set(element._1, element._2)
              result
          })
      }.getOrElse(arbitraryObj)
    }
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    if (node.isObject) {
      properties.map {
        props =>
          props.foldLeft(VerifyResult.success) {
            case (result, (name, schema)) =>
              val field = Option(node.get(name)).getOrElse(nodeFactory.nullNode())
              if (!field.isNull || required.exists(_.contains(name)))
                result.combine(schema.verify(ctx, path :+ name, field))
              else
                VerifyResult.success
          }
      }.getOrElse(VerifyResult.success)
    } else {
      VerifyResult.error(s"${node.getNodeType} should be an object: ${path.mkString(".")}")
    }
  }
}
