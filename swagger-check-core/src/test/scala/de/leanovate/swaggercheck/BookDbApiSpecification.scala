package de.leanovate.swaggercheck

import java.util.UUID

import de.leanovate.swaggercheck.fixtures.bookdb.Author
import de.leanovate.swaggercheck.schema.ValidationResultToProp._
import de.leanovate.swaggercheck.simple._
import org.scalacheck.Prop.{BooleanOperators, forAll}
import org.scalacheck.{Arbitrary, Properties, Shrink}
import play.api.libs.json.{JsSuccess, Json}

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

  property("Request generator POST /author") = {
    val verifier = swaggerChecks.jsonVerifier("Author")

    forAll(swaggerChecks.requestGenerator("POST", "/v1/authors")) {
      request =>
        (request.method == "POST") :| "Method" &&
          (request.path == "/v1/authors") :| "Path" &&
          request.body.isDefined :| "Has body" &&
          verifier.verify(request.body.get.minified).isSuccess :| "Body is author"
    }
  }

  property("Request generator GET /author/{id}") =
    forAll(swaggerChecks.requestGenerator("GET", "/v1/authors/{id}")) {
      request =>
        (request.method == "GET") :| "Method" &&
          request.path.startsWith("/v1/authors") :| "Path" &&
          (UUID.fromString(request.path.substring(12)) ne null) :| "Id is uuid" &&
          request.body.isEmpty :| "Has no body"
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
