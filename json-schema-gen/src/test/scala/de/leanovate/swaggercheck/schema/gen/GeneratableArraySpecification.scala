package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.model.{IntegerDefinition, ArrayDefinition}
import org.scalacheck.Properties

object GeneratableArraySpecification extends Properties("GeneratableArray") with DefinitionChecks {
  property("any generates are valid") = {
    val definition = ArrayDefinition(None, None, None)

    checkDefinition(definition)
  }

  property("generates with item definition are valid") = {
    val definition = ArrayDefinition(None, None, Some(IntegerDefinition(None, None, None)))

    checkDefinition(definition)
  }

  property("generate with minLength are valid") = {
    val definition = ArrayDefinition(Some(10), None, None)

    checkDefinition(definition)
  }

  property("generate with maxLength are valid") = {
    val definition = ArrayDefinition(None, Some(20), None)

    checkDefinition(definition)
  }
}
