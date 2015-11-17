package de.leanovate.swaggercheck.schema.play

import de.leanovate.swaggercheck.schema.model.DefaultSchema
import de.leanovate.swaggercheck.schema.play.model.ProductModel
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import play.api.libs.json.Json
import de.leanovate.swaggercheck.schema.play.DefinitionFormats._
import de.leanovate.swaggercheck.schema.gen.GeneratableDefaultSchema._

object ValidatingReadsSpecification extends Properties("ValidatingReads") {
  val schema = Json.parse(getClass.getClassLoader.getResourceAsStream("schema/simple1.json")).as[DefaultSchema]

  val valdiatingReads = ValidatingReads.validating[Seq[ProductModel]](schema)

  property("any generated can be deserialized") = forAll(schema.generate) {
    json : CheckJsValue =>
      Json.parse(json.minified).validate[Seq[ProductModel]].isSuccess
  }

  property("any generated can be validated") = forAll(schema.generate) {
    json : CheckJsValue =>
      Json.parse(json.minified).validate[Seq[ProductModel]](valdiatingReads).isSuccess
  }
}
