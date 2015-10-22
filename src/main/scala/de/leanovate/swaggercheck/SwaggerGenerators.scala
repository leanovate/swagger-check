package de.leanovate.swaggercheck

import java.io.InputStream

import io.swagger.models.Swagger
import io.swagger.parser.SwaggerParser
import org.scalacheck.Gen

import scala.io.Source

class SwaggerGenerators(swagger: Swagger) {

  def genJson(name: String): Gen[String] = GenSwaggerJson.modelJsonGen(swagger.getDefinitions.get(name))
}

object SwaggerGenerators {
  def apply(swaggerAsString: String) :SwaggerGenerators= {
    val swagger = new SwaggerParser().parse(swaggerAsString)

    new SwaggerGenerators(swagger)
  }

  def apply(swaggerInput: InputStream) :SwaggerGenerators= {
    apply(Source.fromInputStream(swaggerInput).mkString)
  }
}