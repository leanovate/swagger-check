package de.leanovate.swaggercheck

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.leanovate.swaggercheck.formats.{Format, IntegerFormats, NumberFormats, StringFormats}
import de.leanovate.swaggercheck.parser.SwaggerAPI
import org.scalacheck.Gen

case class SwaggerContext(
                           swaggerAPI: SwaggerAPI,
                           stringFormats: Map[String, Format[String]] = StringFormats.defaultFormats,
                           integerFormats: Map[String, Format[Long]] = IntegerFormats.defaultFormats,
                           numberFormats: Map[String, Format[Double]] = NumberFormats.defaultFormats
                           ) {
  val nodeFactory = JsonNodeFactory.instance

  def arbitraryObj: Gen[JsonNode] = for {
    size <- Gen.choose(0, 10)
    properties <- Gen.listOfN(size, arbitraryProperty)
  } yield
    properties.foldLeft(nodeFactory.objectNode()) {
      (result, prop) =>
        result.set(prop._1, prop._2)
        result
    }

  def arbitraryArray: Gen[JsonNode] = for {
    size <- Gen.choose(0, 10)
    items <- Gen.listOfN(size, arbitraryValue)
  } yield
    items.foldLeft(nodeFactory.arrayNode()) {
      (result, value) =>
        result.add(value)
    }

  def arbitraryValue: Gen[JsonNode] = Gen.oneOf(
    Gen.alphaStr.map(nodeFactory.textNode),
    Gen.posNum[Int].map(nodeFactory.numberNode),
    Gen.oneOf(nodeFactory.booleanNode(true), nodeFactory.booleanNode(false))
  )

  def arbitraryProperty: Gen[(String, JsonNode)] = for {
    key <- Gen.identifier
    value <- arbitraryValue
  } yield key -> value
}

