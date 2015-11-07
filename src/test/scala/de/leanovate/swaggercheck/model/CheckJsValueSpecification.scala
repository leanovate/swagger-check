package de.leanovate.swaggercheck.model

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.{SchemaObject, SwaggerAPI}
import org.scalacheck.Prop._
import org.scalacheck.Properties

object CheckJsValueSpecification extends Properties("JsValueDeserializer") {
  val swaggerChecks = SwaggerChecks(SwaggerAPI(None, Map.empty, Map.empty))

  property("arbitraryObject") = forAllNoShrink(SchemaObject.arbitraryObj(swaggerChecks).map(_.toString)) {
    json: String =>
      val jsValue = CheckJsValue.parse(json)

      jsValue.isInstanceOf[CheckJsObject] && jsValue.minified == json
      CheckJsValue.parse(jsValue.prettyfied) == jsValue
  }

  property("arbitraryArray") = forAllNoShrink(SchemaObject.arbitraryArray(swaggerChecks).map(_.toString)) {
    json: String =>
      val jsValue = CheckJsValue.parse(json)

      jsValue.isInstanceOf[CheckJsArray] && jsValue.minified == json
      CheckJsValue.parse(jsValue.prettyfied) == jsValue
  }
}
