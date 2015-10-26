package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.fixtures.model.{Thing, ServiceDocument}
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Properties}
import play.api.libs.json.Json

object ThingApiSpecification extends Properties("Thing API") {
  val swaggerChecks = SwaggerChecks(getClass.getClassLoader.getResourceAsStream("thing_api.yaml"))

  property("Service document is correctly written") = {
    val verifier = swaggerChecks.jsonVerifier("ServiceDocument")

    forAll(Arbitrary.arbitrary[ServiceDocument]) {
      serviceDocument: ServiceDocument =>
        val json = Json.stringify(Json.toJson(serviceDocument))

        verifier.verify(json)
    }
  }

  property("Thing is correctly written") = {
    val verifier = swaggerChecks.jsonVerifier("Thing")

    forAll(Arbitrary.arbitrary[Thing]) {
      thing: Thing =>
        val json = Json.stringify(Json.toJson(thing))

        verifier.verify(json)
    }
  }
}
