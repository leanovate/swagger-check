package de.leanovate.swaggercheck

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.leanovate.swaggercheck.formats.Format
import de.leanovate.swaggercheck.parser.SwaggerAPI

case class SwaggerContext(
                           swaggerAPI: SwaggerAPI,
                           stringFormats: Map[String, Format[String]],
                           integerFormats: Map[String, Format[Long]],
                           numberFormats: Map[String, Format[Double]]
                           ) {
  val nodeFactory = JsonNodeFactory.instance
}

