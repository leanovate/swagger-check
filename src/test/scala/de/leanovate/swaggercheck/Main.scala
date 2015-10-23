package de.leanovate.swaggercheck


import java.util.UUID

import com.fasterxml.jackson.databind.node.JsonNodeFactory

object Main {
  val nodeFactory = JsonNodeFactory.instance

  def main(args: Array[String]): Unit = {
    println(UUID.randomUUID().toString)
    println(GenRegexMatch("[0-9a-f]{8}(\\-[0-9a-f]{4}){3}\\-[0-9a-f]{12}").sample)
    //    val swaggerGenerators = SwaggerGenerators(getClass.getResourceAsStream("/uber_api.yml"))
    //
    //    val json1 = swaggerGenerators.genJson("PriceEstimate")
    //
    //    println(json1.sample)
  }

}
