package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.TestSchema
import de.leanovate.swaggercheck.schema.ValidationResultToProp._
import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.model.{IntegerDefinition, JsonPath}
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Prop._
import org.scalacheck.Properties

object GeneratableIntegerSpecification extends Properties("GeneratableInteger") {
  val schema = TestSchema()

  property("any generate are valid") = {
    val definition = IntegerDefinition(None, None, None)

    forAll(definition.generate(schema)) {
      json: CheckJsValue =>
        val value = CheckJsValue.parse(json.minified)

        definition.validate(schema, JsonPath(), value)
    }
  }

  property("generate with format are valid") = {
    val definition = IntegerDefinition(Some("int32"), None, None)

    forAll(definition.generate(schema)) {
      json: CheckJsValue =>
        val value = CheckJsValue.parse(json.minified)

        definition.validate(schema, JsonPath(), value)
    }
  }

  property("generate with min are valid") = {
    val definition = IntegerDefinition(None, Some(12345), None)

    forAll(definition.generate(schema)) {
      json: CheckJsValue =>
        val value = CheckJsValue.parse(json.minified)

        definition.validate(schema, JsonPath(), value)
    }
  }

  property("generate with max are valid") = {
    val definition = IntegerDefinition(None, None, Some(12345))

    forAll(definition.generate(schema)) {
      json: CheckJsValue =>
        val value = CheckJsValue.parse(json.minified)

        definition.validate(schema, JsonPath(), value)
    }
  }
}
