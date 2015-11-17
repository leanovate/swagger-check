package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.model.NumberDefinition
import org.scalacheck.Properties

object GeneratableNumberSpecification extends Properties("GeneratableNumber") with DefinitionChecks{

  property("any generate are valid") = {
    val definition = NumberDefinition(None, None, None)

    checkDefinition(definition)
  }

  property("generate with format are valid") = {
    val definition = NumberDefinition(Some("double"), None, None)

    checkDefinition(definition)
  }
}
