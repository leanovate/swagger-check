package de.leanovate.swaggercheck.schema

import java.io.{FileInputStream, InputStream}

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.{DeserializationFeature, MappingJsonFactory, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.io.Source

case class SwaggerAPI(
                       definitions: Map[String, SchemaObject]
                       )

object SwaggerAPI {
  def parse(jsonOrYaml: String): SwaggerAPI = {
    val mapper = if (jsonOrYaml.trim().startsWith("{"))
      objectMapper(new MappingJsonFactory())
    else
      objectMapper(new YAMLFactory())
    mapper.readValue(jsonOrYaml, classOf[SwaggerAPI])
  }

  def parse(swaggerInput: InputStream): SwaggerAPI = {
    parse(Source.fromInputStream(swaggerInput).mkString)
  }

  def main(args: Array[String]): Unit = {
    val swagger = parse(new FileInputStream("./src/test/resources/thing_api.yaml"))

    println(swagger)
  }

  def objectMapper(jsonFactory: JsonFactory): ObjectMapper = {
    val mapper = new ObjectMapper(jsonFactory)
    mapper.registerModule(DefaultScalaModule)
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    mapper
  }
}