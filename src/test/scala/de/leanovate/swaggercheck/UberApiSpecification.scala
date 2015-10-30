package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.fixtures.uber.UberError
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import play.api.libs.json.Json

object UberApiSpecification extends Properties("Uber API") {
  val swaggerChecks = SwaggerChecks(getClass.getClassLoader.getResourceAsStream("uber_api.yaml"))

  property("Error can be read") = forAll(swaggerChecks.jsonGenerator("Error")) {
    json =>
      Json.parse(json).validate[UberError].isSuccess
  }

//  property("Request endpoints exists") = forAll(swaggerChecks.requestGenerator[SimpleRequest](None)) {
//    simpleRequest =>
//      println(simpleRequest)
//      true
//  }
}
