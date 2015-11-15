package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.schema.gen.GeneratableSchema
import de.leanovate.swaggercheck.schema.gen.formats.{NumberFormats, IntegerFormats, GeneratableFormat, StringFormats}
import de.leanovate.swaggercheck.schema.model.Definition

case class TestSchema(maxItems: Int = 20) extends GeneratableSchema {
  override def withMaxItems(newMaxItems: Int): TestSchema = TestSchema(newMaxItems)

  override def findGeneratableStringFormat(format: String): Option[GeneratableFormat[String]] = format match {
    case "uuid" => Some(StringFormats.UUIDString)
    case _ => None
  }

  override def findGeneratableNumberFormat(format: String): Option[GeneratableFormat[BigDecimal]] = format match {
    case "double" => Some(NumberFormats.DoubleNumber)
    case _ => None
  }

  override def findGeneratableIntegerFormat(format: String): Option[GeneratableFormat[BigInt]] = format match {
    case "int32" => Some(IntegerFormats.Int32)
    case _ => None
  }

  override def findByRef(ref: String): Option[Definition] = None
}
