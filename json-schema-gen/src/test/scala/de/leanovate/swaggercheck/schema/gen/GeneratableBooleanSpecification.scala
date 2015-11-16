package de.leanovate.swaggercheck.schema.gen

import org.scalacheck.Properties

object GeneratableBooleanSpecification extends Properties("GeneratableBoolean") with DefinitionChecks {
  val definition = GeneratableBoolean

  property("all generated are valid") = checkDefinition(definition)
}
