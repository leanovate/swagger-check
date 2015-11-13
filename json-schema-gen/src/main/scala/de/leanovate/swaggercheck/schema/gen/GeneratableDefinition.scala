package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.model.{ArrayDefinition, Definition}
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Gen
import scala.language.implicitConversions

trait GeneratableDefinition extends Definition {
  def generate(model: GeneratableSchema): Gen[CheckJsValue]
}

object GeneratableDefinition {
  implicit def toGeneratable(definition: Definition): GeneratableDefinition = definition match {
    case arrayDefintiion: ArrayDefinition => GeneratableArray(arrayDefintiion)
  }
}