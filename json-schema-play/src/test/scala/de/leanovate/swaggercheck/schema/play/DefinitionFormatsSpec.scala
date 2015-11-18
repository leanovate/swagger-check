package de.leanovate.swaggercheck.schema.play

import de.leanovate.swaggercheck.schema.model.{Definition, ObjectDefinition, StringDefinition}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.{JsSuccess, Json}
import de.leanovate.swaggercheck.schema.play.Implicits._

class DefinitionFormatsSpec extends WordSpec with MustMatchers {
  "DefinitionFormats" should {
    "deserialize object_definition" in {
      val json = Json.parse(getClass.getClassLoader.getResourceAsStream("object_definition.json"))

      val JsSuccess(definition, _) = json.validate[Definition]
      val ObjectDefinition(required, properties, additionalProperties) = definition

      required mustBe Some(Set("field1"))
      properties mustBe Some(Map("field1" -> StringDefinition(None, None, None, None, None)))
      additionalProperties mustBe Left(true)
    }

  }
}
