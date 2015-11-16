package de.leanovate.swaggercheck.shrinkable

import de.leanovate.swaggercheck.TestSchema
import org.scalacheck.Prop._
import org.scalacheck.Properties

object CheckJsValueSpecification extends Properties("CheckJsValueSpecification") {
  val schema = TestSchema()

  property("arbitraryObject") = forAllNoShrink(schema.arbitraryObj.map(_.minified)) {
    json: String =>
      val jsValue = CheckJsValue.parse(json)

      jsValue.isInstanceOf[CheckJsObject] && jsValue.minified == json
      CheckJsValue.parse(jsValue.prettyfied) == jsValue
  }

  property("arbitraryArray") = forAllNoShrink(schema.arbitraryArray.map(_.minified)) {
    json: String =>
      val jsValue = CheckJsValue.parse(json)

      jsValue.isInstanceOf[CheckJsArray] && jsValue.minified == json
      CheckJsValue.parse(jsValue.prettyfied) == jsValue
  }
}
