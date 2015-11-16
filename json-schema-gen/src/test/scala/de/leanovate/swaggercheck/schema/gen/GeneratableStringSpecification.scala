package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.model.StringDefinition
import org.scalacheck.Properties

object GeneratableStringSpecification extends Properties("GeneratableString") with DefinitionChecks {
  property("any generate are valid") = {
    val definition = StringDefinition(None, None, None, None, None)

    checkDefinition(definition)
  }

  property("generate with format are valid") = {
    val definition = StringDefinition(Some("uuid"), None, None, None, None)

    checkDefinition(definition)
  }

  property("generate with minLength are valid") = {
    val definition = StringDefinition(None, Some(10), None, None, None)

    checkDefinition(definition)
  }

  property("generate with maxLength are valid") = {
    val definition = StringDefinition(None, None, Some(100), None, None)

    checkDefinition(definition)
  }

  property("generate with pattern are valid") = {
    val definition = StringDefinition(None, None, None, Some("[a-zA-Z0-9\\.]+@[a-z]+\\.[a-z]+"), None)

    checkDefinition(definition)
  }

  property("generate with enum are valid") = {
    val definition = StringDefinition(None, None, None, None, Some("foo" :: "bar" :: "grah" :: Nil))

    checkDefinition(definition)
  }
}
