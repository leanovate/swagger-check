package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.model.formats.ValueFormat

trait SchemaModel {
  def getByRef(ref: String): SchemaObject

  def getStringFormat(name: String): Option[ValueFormat[String]]

  def getIntegerFormat(name: String): Option[ValueFormat[BigInt]]

  def getNumberFormat(name: String): Option[ValueFormat[BigDecimal]]
}
