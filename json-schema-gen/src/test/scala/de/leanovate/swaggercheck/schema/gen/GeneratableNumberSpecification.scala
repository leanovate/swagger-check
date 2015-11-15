package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.TestSchema
import de.leanovate.swaggercheck.schema.ValidationResultToProp._
import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.model.{JsonPath, NumberDefinition}
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Prop._
import org.scalacheck.Properties

object GeneratableNumberSpecification extends Properties("GeneratableNumber") {
  val schema = TestSchema()

  property("generate with format are valid") = {
    val definition = NumberDefinition(Some("double"), None, None)

    forAll(definition.generate(schema)) {
      json: CheckJsValue =>
        val value = CheckJsValue.parse(json.minified)

        definition.validate(schema, JsonPath(), value)
    }
  }
}
