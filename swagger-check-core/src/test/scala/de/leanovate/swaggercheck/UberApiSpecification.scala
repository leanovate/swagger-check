package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.fixtures.uber.{UberError, UberProduct}
import de.leanovate.swaggercheck.simple._
import org.scalacheck.Prop.{BooleanOperators, forAll}
import org.scalacheck.{Arbitrary, Gen, Properties}
import play.api.libs.json.Json
import de.leanovate.swaggercheck.schema.gen.ValidationResultToProp._

object UberApiSpecification extends Properties("Uber API") {
  val swaggerChecks = SwaggerChecks(getClass.getClassLoader.getResourceAsStream("uber_api.yaml"))

  property("Error can be read") = forAll(swaggerChecks.jsonGenerator("Error")) {
    json =>
      Json.parse(json.minified).validate[UberError].isSuccess
  }

  property("Error can be written") = {
    val verifier = swaggerChecks.jsonVerifier("Error")

    forAll(Arbitrary.arbitrary[UberError]) {
      error: UberError =>
        val json = Json.stringify(Json.toJson(error))

        verifier.verify(json)
    }
  }

  property("Product can be read") = forAll(swaggerChecks.jsonGenerator("Product")) {
    json =>
      Json.parse(json.minified).validate[UberProduct].isSuccess
  }

  property("Product can be written") = {
    val verifier = swaggerChecks.jsonVerifier("Product")

    forAll(Arbitrary.arbitrary[UberProduct]) {
      product: UberProduct =>
        val json = Json.stringify(Json.toJson(product))

        verifier.verify(json)
    }
  }

  property("Request endpoints exists") = forAll(swaggerChecks.requestGenerator[SimpleRequest]()) {
    case SimpleRequest("GET", "/v1/estimates/price", queryParameters, headers, _) =>
      val paramNames = queryParameters.map(_._1).toSet
      (headers.head == "Accept" -> "application/json") :| "Accept header" &&
        paramNames.contains("start_latitude") :| "paramNames contains start_latitude" &&
        paramNames.contains("start_longitude") :| "paramNames contains start_longitude" &&
        paramNames.contains("end_latitude") :| "paramNames contains end_latitude" &&
        paramNames.contains("end_longitude") :| "paramNames contains end_longitude" &&
        (paramNames.size == 4) :| "paramNames size 4"
    case SimpleRequest("GET", "/v1/estimates/time", queryParameters, headers, _) =>
      val paramNames = queryParameters.map(_._1).toSet
      (headers.head == "Accept" -> "application/json") :| "Accept header" &&
        paramNames.contains("start_latitude") :| "paramNames contains start_latitude" &&
        paramNames.contains("start_longitude") :| "paramNames contains start_longitude" &&
        (paramNames.size <= 4) :| "paramNames size 4"
    case SimpleRequest("GET", "/v1/me", queryParameters, headers, _) =>
      (headers.head == "Accept" -> "application/json") :| "Accept header" &&
        queryParameters.isEmpty :| "query parameter is empty"
    case SimpleRequest("GET", "/v1/history", queryParameters, headers, _) =>
      (headers.head == "Accept" -> "application/json") :| "Accept header" &&
        (queryParameters.size <= 2) :| "query parameter is empty"
    case SimpleRequest("GET", "/v1/products", queryParameters, headers, _) =>
      val paramNames = queryParameters.map(_._1).toSet
      (headers.head == "Accept" -> "application/json") :| "Accept header" &&
        paramNames.contains("latitude") :| "paramNames contains latitude" &&
        paramNames.contains("longitude") :| "paramNames contains longitude" &&
        (paramNames.size <= 2) :| "paramNames size 2"
    case _ => false :| "Does not match any request"
  }

  property("Responses can be verified") = {
    val verifier = swaggerChecks.responseVerifier[SimpleResponse]("GET", "/v1/products")
    val okRepsonseGen = Gen.listOf(Arbitrary.arbitrary[UberProduct])
      .map(products => SimpleResponse(200, Map.empty, Json.stringify(Json.toJson(products))))
    val errorResponseGen = for {
      status <- Gen.choose(400, 599)
      error <- Arbitrary.arbitrary[UberError]
    } yield SimpleResponse(status, Map.empty, Json.stringify(Json.toJson(error)))

    forAll(Gen.oneOf(okRepsonseGen, errorResponseGen)) {
      response: SimpleResponse =>
        verifier.verify(response)
    }
  }

  property("Operation verifier") = forAll(swaggerChecks.operationVerifier[SimpleRequest, SimpleResponse](_ == "/v1/me")) {
    operationVerifier: SimpleOperationVerifier =>
      val profileJson = swaggerChecks.jsonGenerator("Profile")
      val response = SimpleResponse(200, Map.empty, profileJson.sample.get.minified)

      (operationVerifier.request.path == "/v1/me") :| "Path" &&
        (operationVerifier.request.method == "GET") :| "Method" &&
        operationVerifier.responseVerifier.verify(response).isSuccess :| "Response verifier"
  }
}
