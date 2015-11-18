package de.leanovate.swaggercheck.schema.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import de.leanovate.swaggercheck.schema.model.{StringDefinition, ObjectDefinition, Definition}
import org.scalatest.{MustMatchers, WordSpec}

class SchemaModuleSpec extends WordSpec with MustMatchers {
  val mapper = new ObjectMapper().registerModule(DefaultScalaModule).registerModule(JsonSchemaModule)

  "SchemaModule" should {
    "deserialize object_definition" in {
      val ObjectDefinition(required, properties, additionalProperties) = mapper.readValue(getClass.getClassLoader.getResource("object_definition.json"), classOf[Definition])

      required mustBe Some(Set("field1"))
      properties mustBe Some(Map("field1" -> StringDefinition(None, None, None, None, None)))
      additionalProperties mustBe Left(true)
    }
  }
}
