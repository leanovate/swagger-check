package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.model._
import org.scalacheck.Properties

object GeneratableAllOfSpecification extends Properties("GeneratableAllOf") with DefinitionChecks {
  property("generated are value") = {
    val definition = AllOfDefinition(Seq(
      ObjectDefinition(Some(Set("field1")), Some(Map(
        "field1" -> StringDefinition(None, None, None, None, None),
        "field2" -> IntegerDefinition(None, None, None),
        "field3" -> BooleanDefinition
      )), None),
      ObjectDefinition(Some(Set("field4")), Some(Map(
        "field4" -> StringDefinition(None, None, None, None, None),
        "field5" -> IntegerDefinition(None, None, None)
      )), None)
    ))

    checkDefinition(definition)
  }
}
