package de.leanovate.swaggercheck.schema.model

trait SchemaModel {
  def getByRef(ref: String): SchemaObject
}
