package de.leanovate.swaggercheck.schema

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.shrinkable._
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object SchemaObjectSpecification extends Properties("SchemaObject") {
  val swaggerChecks = SwaggerChecks(SwaggerAPI(None, Map.empty, Map.empty))

  property("arbitraryObject") = forAll(swaggerChecks.arbitraryObj) {
    node: CheckJsValue =>
      node.isInstanceOf[CheckJsObject]
  }

  property("arbitraryArray") = forAll(swaggerChecks.arbitraryArray) {
    node: CheckJsValue =>
      node.isInstanceOf[CheckJsArray]
  }

  property("arbitraryValue") = forAll(swaggerChecks.arbitraryValue) {
    node: CheckJsValue =>
      node.isInstanceOf[CheckJsString] || node.isInstanceOf[CheckJsInteger] || node.isInstanceOf[CheckJsBoolean]
  }
}
