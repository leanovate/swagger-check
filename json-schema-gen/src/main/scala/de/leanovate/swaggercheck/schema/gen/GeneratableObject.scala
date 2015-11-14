package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.model.{JsonPath, ObjectDefinition, Schema, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsNull, CheckJsObject, CheckJsValue}
import org.scalacheck.Gen
import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import scala.collection.JavaConversions._

case class GeneratableObject(
                              definition: ObjectDefinition
                            ) extends GeneratableDefinition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult =
    definition.validate(schema, path, node)

  override def generate(schema: GeneratableSchema): Gen[CheckJsValue] = {
    if (definition.properties.isEmpty) {
      definition.additionalProperties match {
        case Some(additionalDefinition) =>
          for {
            size <- Gen.choose(0, schema.maxItems)
            properties <- Gen.listOfN(size, Gen.zip(Gen.identifier, additionalDefinition.generate(schema.childContext)))
          } yield CheckJsObject(Set.empty, None, properties.toMap)
        case None =>
          schema.arbitraryObj
      }
    } else {
      definition.properties.map {
        props =>
          val propertyGens: Traversable[Gen[(String, CheckJsValue)]] = {
            props.map {
              case (name, fieldDefinition) if definition.required.exists(_.contains(name)) =>
                fieldDefinition.generate(schema.childContext).map(value => name -> value)
              case (name, fieldDefinition) =>
                Gen.oneOf(
                  Gen.const(CheckJsNull),
                  fieldDefinition.generate(schema.childContext)
                ).map(value => name -> value)
            }
          }
          Gen.sequence(propertyGens).map {
            fields =>
              CheckJsObject(definition.required.getOrElse(Set.empty), None, fields.toMap)
          }
      }.getOrElse(schema.arbitraryObj)
    }
  }
}
