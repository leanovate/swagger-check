package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.model._
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Gen

import scala.language.implicitConversions

trait GeneratableDefinition extends Definition {
  def generate(schema: GeneratableSchema): Gen[CheckJsValue]
}

object GeneratableDefinition {
  implicit def toGeneratable(definition: Definition): GeneratableDefinition = definition match {
    case definition: AllOfDefinition => GeneratableAllOf(definition)
    case definition: ArrayDefinition => GeneratableArray(definition)
    case BooleanDefinition => GeneratableBoolean
    case definition: IntegerDefinition => GeneratableInteger(definition)
    case definition: NumberDefinition => GeneratableNumber(definition)
    case definition: ObjectDefinition => GeneratableObject(definition)
  }
}