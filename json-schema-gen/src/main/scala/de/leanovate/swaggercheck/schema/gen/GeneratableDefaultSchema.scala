package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.gen.formats.{GeneratableFormat, GeneratableIntegerFormats, GeneratableNumberFormats, GeneratableStringFormats}
import de.leanovate.swaggercheck.schema.model.{DefaultSchema, Definition}
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
import org.scalacheck.Gen

import scala.language.implicitConversions

case class GeneratableDefaultSchema(
                                     schema: DefaultSchema,
                                     maxItems: Int = 10,
                                     stringFormats: Map[String, GeneratableFormat[String]] = GeneratableStringFormats.defaultFormats,
                                     integerFormats: Map[String, GeneratableFormat[BigInt]] = GeneratableIntegerFormats.defaultFormats,
                                     numberFormats: Map[String, GeneratableFormat[BigDecimal]] = GeneratableNumberFormats.defaultFormats
                                   ) extends GeneratableSchema {
  def generate: Gen[CheckJsValue] = schema.root.generate(this)

  override def withMaxItems(newMaxItems: Int): GeneratableSchema = copy(maxItems = maxItems / 2)

  override def findGeneratableStringFormat(format: String): Option[GeneratableFormat[String]] =
    stringFormats.get(format)

  override def findGeneratableNumberFormat(format: String): Option[GeneratableFormat[BigDecimal]] =
    numberFormats.get(format)

  override def findGeneratableIntegerFormat(format: String): Option[GeneratableFormat[BigInt]] =
    integerFormats.get(format)

  override def findByRef(ref: String): Option[Definition] = schema.findByRef(ref)
}

object GeneratableDefaultSchema {
  implicit def toGeneratable(schema: DefaultSchema): GeneratableDefaultSchema = GeneratableDefaultSchema(schema)
}