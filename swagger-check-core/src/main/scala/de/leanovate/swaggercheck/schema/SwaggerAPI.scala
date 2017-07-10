package de.leanovate.swaggercheck.schema

import java.io.InputStream

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.{DeserializationFeature, JsonNode, MappingJsonFactory, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import de.leanovate.swaggercheck.schema.jackson.JsonSchemaModule
import de.leanovate.swaggercheck.schema.model.{Definition, Parameter}

import scala.collection.JavaConversions._
import scala.io.Source

@JsonDeserialize(builder = classOf[SwaggerAPIBuilder])
case class SwaggerAPI(
                       basePath: Option[String],
                       paths: Map[String, Map[String, Operation]],
                       definitions: Map[String, Definition]
                     )

object SwaggerAPI {
  val jsonMapper = objectMapper(new MappingJsonFactory())
  val yamlMapper = objectMapper(new YAMLFactory())

  def parse(jsonOrYaml: String): SwaggerAPI = {
    val mapper = if (jsonOrYaml.trim().startsWith("{")) jsonMapper else yamlMapper
    mapper.readValue(jsonOrYaml, classOf[SwaggerAPI])
  }

  def parse(swaggerInput: InputStream): SwaggerAPI = {
    parse(Source.fromInputStream(swaggerInput).mkString)
  }

  def objectMapper(jsonFactory: JsonFactory): ObjectMapper = {
    val mapper = new ObjectMapper(jsonFactory)
    mapper.registerModule(DefaultScalaModule)
    mapper.registerModule(JsonSchemaModule)
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    mapper
  }
}

class SwaggerAPIBuilder @JsonCreator()(
                                        @JsonProperty("basePath") basePath: Option[String],
                                        @JsonProperty("consumes") consumes: Option[Seq[String]],
                                        @JsonProperty("produces") produces: Option[Seq[String]],
                                        @JsonProperty("paths") paths: Option[Map[String, JsonNode]],
                                        @JsonProperty("definitions") definitions: Option[Map[String, Definition]],
                                        @JsonProperty("parameters") globalParameters: Option[Map[String, Parameter]]
                                      ) {
  def build(): SwaggerAPI = {
    val defaultConsumes = consumes.map(_.toSet).getOrElse(Set.empty)
    val defaultProduces = produces.map(_.toSet).getOrElse(Set.empty)
    SwaggerAPI(basePath,
      paths.getOrElse(Map.empty).map {
        case (path, pathDefinition) =>
          val defaultParameters = Option(pathDefinition.get("parameters")).map {
             node =>
               node.iterator().map {
                 element => SwaggerAPI.jsonMapper.treeToValue(element, classOf[OperationParameter])
               }.toSeq
          }.getOrElse(Seq.empty)

          basePath.map(_ + path).getOrElse(path) -> pathDefinition.fields().filter(_.getKey != "parameters").map {
            entry =>
              val operation = SwaggerAPI.jsonMapper.treeToValue(entry.getValue, classOf[Operation])
              entry.getKey.toUpperCase -> operation.withDefaults(defaultParameters, defaultConsumes, defaultProduces).resolveGlobalParameters(globalParameters.getOrElse(Map()))
          }.toMap
      },
      definitions.getOrElse(Map.empty))
  }
}