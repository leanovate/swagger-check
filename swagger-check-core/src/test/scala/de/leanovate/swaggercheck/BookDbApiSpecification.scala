package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.ThingApiSpecification._
import de.leanovate.swaggercheck.ThingApiSpecification.swaggerChecks
import de.leanovate.swaggercheck.UberApiSpecification._
import de.leanovate.swaggercheck.fixtures.bookdb.Author
import de.leanovate.swaggercheck.fixtures.model.Thing
import org.scalacheck.{Shrink, Arbitrary, Properties}
import play.api.libs.json.Json
import org.scalacheck.Prop.{BooleanOperators, forAll}
import de.leanovate.swaggercheck.schema.ValidationResultToProp
import de.leanovate.swaggercheck.simple._
import ValidationResultToProp._

object BookDbApiSpecification extends Properties("BookDB API") {
  val swaggerChecks = SwaggerChecks(getClass.getClassLoader.getResourceAsStream("bookdb_api.yaml"))

  property("Author is correctly written") = {
    val verifier = swaggerChecks.jsonVerifier("Author")

    forAll(Arbitrary.arbitrary[Author]) {
      author: Author =>
        val json = Json.stringify(Json.toJson(author))

        verifier.verify(json)
    }
  }

  property("Author can be correctly parsed") = {
    val verifier = swaggerChecks.jsonVerifier("Author")

    forAll(swaggerChecks.jsonGenerator("Author")) {
      json =>
        Json.parse(json.minified).validate[Author].isSuccess :| "Json can be deserialized" &&
          verifier.verify(json.minified).isSuccess :| "Json conforms to own schema" &&
          Shrink.shrink(json).forall {
            shrinked =>
              verifier.verify(shrinked.minified).isSuccess
          } :| "All shrinked variants conform to schema"
    }
  }

  property("Operation verifier") = forAll(swaggerChecks.operationVerifier[SimpleRequest, SimpleResponse](_ == "/v1/authors")) {
    case operationVerifier: SimpleOperationVerifier if operationVerifier.request.method == "GET" =>
      val profileJson = swaggerChecks.jsonGenerator("AuthorsPage")
      val response = SimpleResponse(200, Map.empty, profileJson.sample.get.minified)

      (operationVerifier.request.path == "/v1/authors") :| "Path" &&
        (operationVerifier.request.method == "GET") :| "Method" &&
        operationVerifier.responseVerifier.verify(response).isSuccess :| "Response verifier"
    case operationVerifier: SimpleOperationVerifier =>
      val profileJson = swaggerChecks.jsonGenerator("Author")
      val response = SimpleResponse(201, Map.empty, "")

      (operationVerifier.request.path == "/v1/authors") :| "Path" &&
        (operationVerifier.request.method == "POST") :| "Method" &&
        operationVerifier.responseVerifier.verify(response).isSuccess :| "Response verifier"
      true :| "Just ok"
  }
}
