package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.{VerifyResult, SwaggerChecks}
import org.scalacheck.Gen

case class CompatAllOf(
                                  schema: SchemaObject,
                                  required: Option[Set[String]],
                                  properties: Option[Map[String, SchemaObject]]
                                  )