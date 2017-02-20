package de.leanovate.swaggercheck.schema.play

import de.leanovate.swaggercheck.schema.model.DefaultSchema
import de.leanovate.swaggercheck.schema.play.Implicits._
import de.leanovate.swaggercheck.schema.play.model.ProductModel
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.{JsError, Json, Reads}

class ValidatingReadsSpec extends WordSpec with MustMatchers {
  val schema: DefaultSchema = Json
    .parse(getClass.getClassLoader.getResourceAsStream("schema/simple1.json"))
    .as[DefaultSchema]

  val atLeastOneTagRead: Reads[Seq[ProductModel]] =
    ValidatingReads.validating[Seq[ProductModel]](schema)

  "ValidatingReads" should {
    "reject invalid json input" in {
      val json = Json.parse("""[
        |    {
        |        "id": 12345678,
        |        "name": "thename",
        |        "price": 1234.67,
        |        "tags": []
        |    }
        |]""".stripMargin)

      val result = json.validate(atLeastOneTagRead)

      result mustBe a[JsError]
    }
  }
}
