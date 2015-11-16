package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.model.formats.ValueFormat

trait Schema {
  def findByRef(ref: String): Option[Definition]

  def findStringFormat(name: String): Option[ValueFormat[String]]

  def findIntegerFormat(name: String): Option[ValueFormat[BigInt]]

  def findNumberFormat(name: String): Option[ValueFormat[BigDecimal]]
}
