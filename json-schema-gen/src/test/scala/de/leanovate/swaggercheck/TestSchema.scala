package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.schema.gen.GeneratableSchema
import de.leanovate.swaggercheck.schema.gen.formats.{GeneratableFormat, GeneratableIntegerFormats, GeneratableNumberFormats, GeneratableStringFormats}
import de.leanovate.swaggercheck.schema.model.Definition

case class TestSchema(
                       randomAdditionalFields:Boolean = false,
                       maxItems: Int = 20
                     ) extends GeneratableSchema {
  val integerFormats = GeneratableIntegerFormats.defaultFormats
  val numberFormats = GeneratableNumberFormats.defaultFormats
  val stringFormats = GeneratableStringFormats.defaultFormats

  override def withMaxItems(newMaxItems: Int): TestSchema = copy(maxItems = newMaxItems)

  override def findGeneratableStringFormat(format: String): Option[GeneratableFormat[String]] =
    stringFormats.get(format)

  override def findGeneratableNumberFormat(format: String): Option[GeneratableFormat[BigDecimal]] =
    numberFormats.get(format)

  override def findGeneratableIntegerFormat(format: String): Option[GeneratableFormat[BigInt]] =
    integerFormats.get(format)

  override def findByRef(ref: String): Option[Definition] = None
}
