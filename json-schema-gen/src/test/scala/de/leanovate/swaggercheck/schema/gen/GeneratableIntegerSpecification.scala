package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.model.IntegerDefinition
import org.scalacheck.Properties

object GeneratableIntegerSpecification extends Properties("GeneratableInteger") with DefinitionChecks {
  property("any generate are valid") = {
    val definition = IntegerDefinition(None, None, None)

    checkDefinition(definition)
  }

  property("generate with format are valid") = {
    val definition = IntegerDefinition(Some("int32"), None, None)

    checkDefinition(definition)
  }

  property("generate with min are valid") = {
    val definition = IntegerDefinition(None, Some(12345), None)

    checkDefinition(definition)
  }

  property("generate with max are valid") = {
    val definition = IntegerDefinition(None, None, Some(12345))

    checkDefinition(definition)
  }
}
