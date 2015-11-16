package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.schema.gen.GeneratableSchema
import de.leanovate.swaggercheck.schema.gen.formats.{GeneratableFormat, IntegerFormats, NumberFormats, StringFormats}
import de.leanovate.swaggercheck.schema.model.Definition

case class TestSchema(maxItems: Int = 20) extends GeneratableSchema {
  val integerFormats = IntegerFormats.defaultFormats
  val numberFormats = NumberFormats.defaultFormats
  val stringFormats = StringFormats.defaultFormats

  override def withMaxItems(newMaxItems: Int): TestSchema = TestSchema(newMaxItems)

  override def findGeneratableStringFormat(format: String): Option[GeneratableFormat[String]] =
    stringFormats.get(format)

  override def findGeneratableNumberFormat(format: String): Option[GeneratableFormat[BigDecimal]] =
    numberFormats.get(format)

  override def findGeneratableIntegerFormat(format: String): Option[GeneratableFormat[BigInt]] =
    integerFormats.get(format)

  override def findByRef(ref: String): Option[Definition] = None
}
