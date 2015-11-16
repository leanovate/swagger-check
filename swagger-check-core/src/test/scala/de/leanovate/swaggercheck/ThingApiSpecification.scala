package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.fixtures.model._
import de.leanovate.swaggercheck.schema.ValidationResultToProp
import de.leanovate.swaggercheck.simple._
import org.scalacheck.Prop.{BooleanOperators, forAll}
import org.scalacheck.{Arbitrary, Properties, Shrink}
import play.api.libs.json.{JsSuccess, Json}
import ValidationResultToProp._

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

  property("Thing can be correctly parsed") = {
    val verifier = swaggerChecks.jsonVerifier("Thing")

    forAll(swaggerChecks.jsonGenerator("Thing")) {
      json =>
        Json.parse(json.minified).validate[Thing].isSuccess :| "Json can be deserialized" &&
          verifier.verify(json.minified).isSuccess :| "Json conforms to own schema" &&
          Shrink.shrink(json).forall {
            shrinked =>
              verifier.verify(shrinked.minified).isSuccess
          } :| "All shrinked variants conform to schema"
    }
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
      Json.parse(json.minified).validate[ThingList].isSuccess
  }

  property("SubBase can be written") = {
    val verifier = swaggerChecks.jsonVerifier("SubBase")

    forAll(Arbitrary.arbitrary[SubBase]) {
      subBase: SubBase =>
        val json = Json.stringify(Json.toJson(subBase))

        verifier.verify(json)
    }
  }

  property("SubBase can be parsed") = forAll(swaggerChecks.jsonGenerator("SubBase")) {
    json =>
      Json.parse(json.minified).validate[SubBase].isSuccess
  }

  property("OtherBase can be parsed") = forAll(swaggerChecks.jsonGenerator("OtherBase")) {
    json =>
      Json.parse(json.minified).validate[OtherBase].isSuccess
  }

  property("ThingNode can be parsed") = forAll(swaggerChecks.jsonGenerator("ThingNode")) {
    json =>
      Json.parse(json.minified).validate[ThingNode].isSuccess
  }

  property("AnyThing can be read") = {
    val verifier = swaggerChecks.jsonVerifier("AnyThing")

    forAll(swaggerChecks.jsonGenerator("AnyThing")) {
      json =>
        val JsSuccess(anyThing, _) = Json.parse(json.minified).validate[AnyThing]

        anyThing.isValid() :| "Json can be deserialized" &&
          verifier.verify(json.minified).isSuccess :| "Json conforms to own schema" &&
          Shrink.shrink(json).forall {
            shrinked =>
              verifier.verify(shrinked.minified).isSuccess
          } :| "All shrinked variants conform to schema"
    }
  }

  property("AnyThing can be written") = {
    val verifier = swaggerChecks.jsonVerifier("AnyThing")

    forAll(Arbitrary.arbitrary[AnyThing]) {
      anyThing: AnyThing =>
        val json = Json.stringify(Json.toJson(anyThing))

        verifier.verify(json)
    }
  }

  property("Operation can be verified") = forAll(swaggerChecks.operationVerifier[SimpleRequest, SimpleResponse]()) {
    operationVerifier: SimpleOperationVerifier =>
      val negativeResponse = SimpleResponse(400, Map.empty, "")

      (operationVerifier.request.path.startsWith("/")) :| "Path startsWith /" &&
        (operationVerifier.request.method == "GET" || operationVerifier.request.method == "POST") :| "Method" &&
        !operationVerifier.responseVerifier.verify(negativeResponse).isSuccess :| "Response verifier"
  }
}
