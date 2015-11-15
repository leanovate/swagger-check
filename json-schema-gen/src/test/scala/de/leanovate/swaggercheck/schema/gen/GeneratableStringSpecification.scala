package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.TestSchema
import de.leanovate.swaggercheck.schema.ValidationResultToProp._
import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.model.{JsonPath, StringDefinition}
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Prop._
import org.scalacheck.Properties

object GeneratableStringSpecification extends Properties("GeneratableString") {
  val schema = TestSchema()

  property("any generate are valid") = {
    val definition = StringDefinition(None, None, None, None, None)

    forAll(definition.generate(schema)) {
      json: CheckJsValue =>
        val value = CheckJsValue.parse(json.minified)

        definition.validate(schema, JsonPath(), value)
    }
  }

  property("generate with form are valid") = {
    val definition = StringDefinition(Some("uuid"), None, None, None, None)

    forAll(definition.generate(schema)) {
      json: CheckJsValue =>
        val value = CheckJsValue.parse(json.minified)

        definition.validate(schema, JsonPath(), value)
    }
  }
}
