package de.leanovate.swaggercheck.schema.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import de.leanovate.swaggercheck.schema.model.{ObjectDefinition, Definition}
import org.scalatest.{MustMatchers, WordSpec}

class SchemaModuleSpec extends WordSpec with MustMatchers {
  val mapper = new ObjectMapper().registerModule(SchemaModule).registerModule(DefaultScalaModule)

  "SchemaModule" should {
    "deserialize object_definition" in {
      val definition = mapper.readValue(getClass.getClassLoader.getResource("object_definition.json"), classOf[Definition])

      definition mustBe an[ObjectDefinition]
      println(definition)
    }
  }
}
