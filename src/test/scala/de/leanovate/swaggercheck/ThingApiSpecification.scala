package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.fixtures.model.{ThingList, Thing, ServiceDocument}
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

  property("Thing can be correctly parsed") = forAll(swaggerChecks.jsonGenerator("Thing")) {
    json =>
      Json.parse(json).validate[Thing].isSuccess
  }

  property("Thing list is correctly written") = {
    val verifier = swaggerChecks.jsonVerifier("ThingList")

    forAll(Arbitrary.arbitrary[ThingList]) {
      thingList: ThingList =>
        val json = Json.stringify(Json.toJson(thingList))

        verifier.verify(json)
    }
  }

  property("Thing list can be correctly parsed") = forAll(swaggerChecks.jsonGenerator("ThingList")) {
    json =>
      Json.parse(json).validate[ThingList].isSuccess
  }
}
