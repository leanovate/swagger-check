package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

@JsonDeserialize(builder = classOf[SchemaObjectBuilder])
trait SchemaObject {
  def generate(context: SwaggerChecks): Gen[JsonNode]

  def verify(context: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult
}

object SchemaObject {
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
