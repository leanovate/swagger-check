package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.model._
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object SchemaObjectSpecification extends Properties("SchemaObject") {
  val swaggerChecks = SwaggerChecks(SwaggerAPI(None, Map.empty, Map.empty))

  property("arbitraryObject") = forAll(SchemaObject.arbitraryObj(swaggerChecks)) {
    node: CheckJsValue =>
      node.isInstanceOf[CheckJsObject]
  }

  property("arbitraryArray") = forAll(SchemaObject.arbitraryArray(swaggerChecks)) {
    node: CheckJsValue =>
      node.isInstanceOf[CheckJsArray]
  }

  property("arbitraryValue") = forAll(SchemaObject.arbitraryValue) {
    node: CheckJsValue =>
      node.isInstanceOf[CheckJsString] || node.isInstanceOf[CheckJsInteger] || node.isInstanceOf[CheckJsBoolean]
  }
}
