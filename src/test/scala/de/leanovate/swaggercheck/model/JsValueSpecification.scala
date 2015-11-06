package de.leanovate.swaggercheck.model

import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.{SchemaObject, SwaggerAPI}
import org.scalacheck.Prop._
import org.scalacheck.Properties

object JsValueSpecification extends Properties("JsValueDeserializer") {
  val swaggerChecks = SwaggerChecks(SwaggerAPI(None, Map.empty, Map.empty))

  property("arbitraryObject") = forAllNoShrink(SchemaObject.arbitraryObj(swaggerChecks).map(_.toString)) {
    json: String =>
      val jsValue = JsValue.parse(json)

      jsValue.isInstanceOf[JsObject] && jsValue.minified == json
  }

  property("arbitraryArray") = forAllNoShrink(SchemaObject.arbitraryArray(swaggerChecks).map(_.toString)) {
    json: String =>
      val jsValue = JsValue.parse(json)

      jsValue.isInstanceOf[JsArray] && jsValue.minified == json
  }
}
