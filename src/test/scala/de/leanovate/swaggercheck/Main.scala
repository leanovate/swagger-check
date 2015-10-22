package de.leanovate.swaggercheck


import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import io.swagger.models.Model
import io.swagger.models.properties._
import io.swagger.parser.SwaggerParser
import org.scalacheck.Gen
import scala.collection.JavaConversions._

import scala.io.Source

object Main {
  val nodeFactory = JsonNodeFactory.instance

  def main(args: Array[String]): Unit = {
    val swaggerGenerators = SwaggerGenerators(getClass.getResourceAsStream("/uber_api.yml"))

    val json1 = swaggerGenerators.genJson("PriceEstimate")

    println(json1.sample)
  }

}
