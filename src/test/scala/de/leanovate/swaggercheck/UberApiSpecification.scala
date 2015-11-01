package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.fixtures.uber.UberError
import de.leanovate.swaggercheck.simple.SimpleRequest
import org.scalacheck.Prop.{BooleanOperators, forAll}
import org.scalacheck.Properties
import play.api.libs.json.Json

object UberApiSpecification extends Properties("Uber API") {
  val swaggerChecks = SwaggerChecks(getClass.getClassLoader.getResourceAsStream("uber_api.yaml"))

  property("Error can be read") = forAll(swaggerChecks.jsonGenerator("Error")) {
    json =>
      Json.parse(json).validate[UberError].isSuccess
  }

  property("Request endpoints exists") = forAll(swaggerChecks.requestGenerator[SimpleRequest](None)) {
    case SimpleRequest("GET", "/estimates/price", queryParameters, headers, _) =>
      val paramNames = queryParameters.map(_._1).toSet
      (headers.head == "Accept" -> "application/json") :| "Accept header" &&
        paramNames.contains("start_latitude") :| "paramNames contains start_latitude" &&
        paramNames.contains("start_longitude") :| "paramNames contains start_longitude" &&
        paramNames.contains("end_latitude") :| "paramNames contains end_latitude" &&
        paramNames.contains("end_longitude") :| "paramNames contains end_longitude" &&
        (paramNames.size == 4) :| "paramNames size 4"
    case SimpleRequest("GET", "/estimates/time", queryParameters, headers, _) =>
      val paramNames = queryParameters.map(_._1).toSet
      (headers.head == "Accept" -> "application/json") :| "Accept header" &&
        paramNames.contains("start_latitude") :| "paramNames contains start_latitude" &&
        paramNames.contains("start_longitude") :| "paramNames contains start_longitude" &&
        (paramNames.size <= 4) :| "paramNames size 4"
    case SimpleRequest("GET", "/me", queryParameters, headers, _) =>
      (headers.head == "Accept" -> "application/json") :| "Accept header" &&
        queryParameters.isEmpty :| "query parameter is empty"
    case SimpleRequest("GET", "/history", queryParameters, headers, _) =>
      (headers.head == "Accept" -> "application/json") :| "Accept header" &&
        (queryParameters.size <= 2) :| "query parameter is empty"
    case SimpleRequest("GET", "/products", queryParameters, headers, _) =>
      val paramNames = queryParameters.map(_._1).toSet
      (headers.head == "Accept" -> "application/json") :| "Accept header" &&
        paramNames.contains("latitude") :| "paramNames contains latitude" &&
        paramNames.contains("longitude") :| "paramNames contains longitude" &&
        (paramNames.size <= 2) :| "paramNames size 2"
    case _ => false :| "Does not match any request"
  }
}
