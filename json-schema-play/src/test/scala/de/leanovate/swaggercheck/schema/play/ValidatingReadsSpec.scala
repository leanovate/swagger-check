package de.leanovate.swaggercheck.schema.play

import de.leanovate.swaggercheck.schema.model.DefaultSchema
import de.leanovate.swaggercheck.schema.play.DefinitionFormats._
import de.leanovate.swaggercheck.schema.play.model.ProductModel
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json

class ValidatingReadsSpec extends WordSpec with MustMatchers {
  val schema = Json.parse(getClass.getClassLoader.getResourceAsStream("schema/simple1.json")).as[DefaultSchema]

  val valdiatingReads = ValidatingReads.validating[Seq[ProductModel]](schema)

  "ValidatingReads" should {
    val json = Json.parse(
      """[
        |    {
        |        "id": 12345678,
        |        "name": "thename",
        |        "price": 1234.67,
        |        "tags": []
        |    }
        |]""".stripMargin)

    json.validate[Seq[ProductModel]].isSuccess mustBe true
    val result = json.validate[Seq[ProductModel]](valdiatingReads)

    result.isSuccess mustBe false
  }
}
