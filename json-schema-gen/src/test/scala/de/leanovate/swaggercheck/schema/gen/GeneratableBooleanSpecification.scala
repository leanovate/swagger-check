package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.TestSchema
import de.leanovate.swaggercheck.schema.ValidationResultToProp
import ValidationResultToProp._
import de.leanovate.swaggercheck.schema.model.JsonPath
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Prop._
import org.scalacheck.Properties

object GeneratableBooleanSpecification extends Properties("GeneratableBoolean") {
  val schema = TestSchema()

  val definition = GeneratableBoolean

  property("all generated are valid") = forAll(definition.generate(schema)) {
    json : CheckJsValue =>
      val value = CheckJsValue.parse(json.minified)

      definition.validate(schema, JsonPath(), value)
  }
}
