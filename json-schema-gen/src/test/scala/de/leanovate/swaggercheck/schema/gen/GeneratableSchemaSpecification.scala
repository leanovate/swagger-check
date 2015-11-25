package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.TestSchema
import de.leanovate.swaggercheck.shrinkable._
import org.scalacheck.Prop._
import org.scalacheck.Properties

object GeneratableSchemaSpecification extends Properties("GeneratableSchema"){
  val schema = TestSchema()

  property("arbitraryObject") = forAll(schema.arbitraryObj) {
    node: CheckJsValue =>
      node.isInstanceOf[CheckJsObject]
  }

  property("arbitraryArray") = forAll(schema.arbitraryArray) {
    node: CheckJsValue =>
      node.isInstanceOf[CheckJsArray]
  }

  property("arbitraryValue") = forAll(schema.arbitraryValue) {
    node: CheckJsValue =>
      node.isInstanceOf[CheckJsString] || node.isInstanceOf[CheckJsInteger] || node.isInstanceOf[CheckJsBoolean]
  }
}
