package de.leanovate.swaggercheck

import de.leanovate.swaggercheck.schema.gen.GeneratableSchema
import de.leanovate.swaggercheck.schema.gen.formats.GeneratableFormat
import de.leanovate.swaggercheck.schema.model.Definition

case class TestSchema(maxItems: Int = 20) extends GeneratableSchema {
  override def withMaxItems(newMaxItems: Int): TestSchema= TestSchema(newMaxItems)

  override def findGeneratableStringFormat(format: String): Option[GeneratableFormat[String]] = None

  override def findGeneratableNumberFormat(format: String): Option[GeneratableFormat[BigDecimal]] = None

  override def findGeneratableIntegerFormat(format: String): Option[GeneratableFormat[BigInt]] = None

  override def findByRef(ref: String): Option[Definition] = None
}
