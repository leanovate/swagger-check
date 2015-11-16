package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.TestSchema
import de.leanovate.swaggercheck.schema.ValidationResultToProp._
import de.leanovate.swaggercheck.schema.model.JsonPath
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Prop._

trait DefinitionChecks {
  val schema = TestSchema()

  def checkDefinition(definition: GeneratableDefinition) = forAll(definition.generate(schema)) {
    json: CheckJsValue =>
      val value = CheckJsValue.parse(json.minified)

      definition.validate(schema, JsonPath(), value)
  }
}
