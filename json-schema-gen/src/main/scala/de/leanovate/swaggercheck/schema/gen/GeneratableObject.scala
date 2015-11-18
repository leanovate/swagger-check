package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.model.{JsonPath, ObjectDefinition, Schema, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsNull, CheckJsObject, CheckJsValue}
import org.scalacheck.Gen

import scala.collection.JavaConversions._

case class GeneratableObject(
                              definition: ObjectDefinition
                            ) extends GeneratableDefinition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult =
    definition.validate(schema, path, node)

  override def generate(schema: GeneratableSchema): Gen[CheckJsValue] = {
    if (definition.properties.isEmpty && definition.additionalProperties.left.exists(_ == true))
      schema.arbitraryObj
    else {
      val propertyGens: Traversable[Gen[(String, CheckJsValue)]] = definition.properties.map(_.map {
        case (name, fieldDefinition) if definition.required.exists(_.contains(name)) =>
          fieldDefinition.generate(schema.childContext).map(value => name -> value)
        case (name, fieldDefinition) =>
          Gen.oneOf(
            Gen.const(CheckJsNull),
            fieldDefinition.generate(schema.childContext)
          ).map(value => name -> value)
      }).getOrElse(Seq.empty)

      val additionalPropertyGen: Gen[Seq[(String, CheckJsValue)]] = definition.additionalProperties match {
        case Right(additionalDefinition) if schema.maxItems > 0 =>
          for {
            size <- Gen.choose(0, schema.maxItems)
            properties <- Gen.listOfN(size, Gen.zip(Gen.identifier, additionalDefinition.generate(schema.childContext)))
          } yield properties
        case Left(true) if schema.randomAdditionalFields =>
          for {
            size <- Gen.choose(0, schema.maxItems)
            properties <- Gen.listOfN(size, schema.arbitraryProperty)
          } yield properties
        case _ =>
          Gen.const(Seq.empty)
      }
      for {
        fields <- Gen.sequence(propertyGens)
        additionalFields <- additionalPropertyGen
      } yield CheckJsObject(definition.required.getOrElse(Set.empty), None, (fields ++ additionalFields).toMap)
    }
  }
}
