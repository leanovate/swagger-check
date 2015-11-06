package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.databind.{ObjectMapper, JsonNode}
import de.leanovate.swaggercheck.SwaggerChecks
import de.leanovate.swaggercheck.schema.{SchemaObject, SwaggerAPI}
import org.scalacheck.Prop._
import org.scalacheck.Properties

object JsValueDeserializerSpecification extends Properties("JsValueDeserializer") {
  val swaggerChecks = SwaggerChecks(SwaggerAPI(None, Map.empty, Map.empty))
  val mapper = new ObjectMapper()

  property("arbitraryObject") = forAll(SchemaObject.arbitraryObj(swaggerChecks).map(_.toString)) {
    json: String =>
      mapper.readValue(json, classOf[JsValue]).isInstanceOf[JsObject]
  }

  property("arbitraryArray") = forAll(SchemaObject.arbitraryArray(swaggerChecks).map(_.toString)) {
    json: String =>
      mapper.readValue(json, classOf[JsValue]).isInstanceOf[JsArray]
  }
}
