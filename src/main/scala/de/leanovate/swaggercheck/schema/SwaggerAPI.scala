package de.leanovate.swaggercheck.schema

import java.io.{FileInputStream, InputStream}

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.io.Source

case class SwaggerAPI(
                       definitions: Map[String, SchemaObject]
                       )

object SwaggerAPI {
  val jsonMapper = new ObjectMapper()
  val yamlMapper = new ObjectMapper(new YAMLFactory())

  jsonMapper.registerModule(DefaultScalaModule)
  jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  yamlMapper.registerModule(DefaultScalaModule)
  yamlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

  def parse(jsonOrYaml: String): SwaggerAPI = {
    val mapper = if (jsonOrYaml.trim().startsWith("{")) jsonMapper else yamlMapper
    mapper.readValue(jsonOrYaml, classOf[SwaggerAPI])
  }

  def parse(swaggerInput: InputStream): SwaggerAPI = {
    parse(Source.fromInputStream(swaggerInput).mkString)
  }

  def main(args: Array[String]): Unit = {
    val swagger = parse(new FileInputStream("./src/test/resources/thing_api.yaml"))

    println(swagger)
  }
}