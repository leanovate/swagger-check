package de.leanovate.swaggercheck

import java.net.{URI, URL}
import java.time.format.DateTimeFormatter
import java.util.UUID

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import io.swagger.models.properties._
import io.swagger.models.{Model, Swagger}

import scala.collection.JavaConversions._
import scala.util.Try

class SwaggerJsonVerifier(swagger: Swagger, rootModel: Model) extends Verifier[String] {

  import SwaggerJsonVerifier._

  override def verify(value: String): VerifyResult = {
    val tree = objectMapper.readTree(value)

    verifyNode("", tree, rootModel.getProperties.toMap)
  }

  def verifyNode(path: String, node: JsonNode, properties: Map[String, Property]): VerifyResult =
    properties.foldLeft(VerifyResult.success) {
      (result, property) =>
        result.combine(verifyProperty(path + property._1, Option(node.get(property._1)), property._2))
    }

  def verifyProperty(path: String, optValue: Option[JsonNode], property: Property): VerifyResult = {
    optValue match {
      case Some(value) if value.isNull && property.getRequired => VerifyResult.error(s"Required value missing: $path")
      case Some(value) if value.isNull => VerifyResult.success
      case Some(value) => verifyPropertyValue(path: String, value, property)
      case None if property.getRequired => VerifyResult.error(s"Required value missing: $path")
      case _ => VerifyResult.success
    }
  }

  def verifyPropertyValue(path: String, value: JsonNode, property: Property): VerifyResult = property match {
    case objectProperty: ObjectProperty if value.isObject =>
      Option(objectProperty.getProperties).map {
        properties =>
          verifyNode(path + ".", value, properties.toMap)
      }.getOrElse(VerifyResult.success)
    case _: ObjectProperty => VerifyResult.error(s"${value.getNodeType} should be an object: $path")
    case arrayProperty: ArrayProperty if value.isArray =>
      Option(arrayProperty.getItems).map {
        itemProperty =>
          Range(0, value.size()).foldLeft(VerifyResult.success) {
            (result, index) =>
              val element = value.get(index)
              result.combine(verifyProperty(path + s"[$index]", Some(element), itemProperty))
          }
      }.getOrElse(VerifyResult.success)
    case _: ArrayProperty => VerifyResult.error(s"${value.getNodeType} should be an array: $path")
    case emailProperty: EmailProperty if value.isTextual =>
      if (emailPattern.pattern.matcher(value.asText()).matches()) {
        VerifyResult.success
      } else {
        VerifyResult.error(s"'${value.asText()}' is not an email: $path")
      }
    case _: EmailProperty => VerifyResult.error(s"${value.getNodeType} should be an email: $path")
    case stringProperty: StringProperty if value.isTextual =>
      val textValue = value.asText()

      if (Option(stringProperty.getMinLength).exists(_ > textValue.length)) {
        VerifyResult.error(s"'$textValue' has to be at least ${stringProperty.getMinLength} chars long: $path")
      } else if (Option(stringProperty.getMaxLength).exists(_ < textValue.length)) {
        VerifyResult.error(s"'$textValue' has to be at most ${stringProperty.getMaxLength} chars long: $path")
      } else if (Option(stringProperty.getPattern).exists(!_.r.pattern.matcher(textValue).matches())) {
        VerifyResult.error(s"'$textValue' has match '${stringProperty.getPattern}': $path")
      } else if (Option(stringProperty.getEnum).exists(!_.contains(textValue))) {
        VerifyResult.error(s"'$textValue' has to be one of ${stringProperty.getEnum}: $path")
      } else {
        Option(stringProperty.getFormat) match {
          case Some("uri") if Try(new URI(textValue)).isFailure =>
            VerifyResult.error(s"'$textValue' has to an uri: $path")
          case Some("url") if Try(new URL(textValue)).isFailure =>
            VerifyResult.error(s"'$textValue' has to an url: $path")
          case _ => VerifyResult.success
        }
      }
    case _: StringProperty => VerifyResult.error(s"${value.getNodeType} should be astring: $path")
    case decimalProperty: DecimalProperty if value.isNumber =>
      val doubleValue = value.asDouble()
      if (Option(decimalProperty.getMinimum).exists(_ > doubleValue)) {
        VerifyResult.error(s"'$doubleValue' has to be greater than ${decimalProperty.getMinimum}: $path")
      } else if (Option(decimalProperty.getMaximum).exists(_ < doubleValue)) {
        VerifyResult.error(s"'$doubleValue' has to be less than ${decimalProperty.getMaximum}: $path")
      } else {
        VerifyResult.success
      }
    case _: DecimalProperty => VerifyResult.error(s"${value.getNodeType} should be number: $path")
    case integerProperty: IntegerProperty if value.isNumber && value.canConvertToInt =>
      val intValue = value.asInt()
      if (Option(integerProperty.getMinimum).exists(_ > intValue)) {
        VerifyResult.error(s"'$intValue' has to be greater than ${integerProperty.getMinimum}: $path")
      } else if (Option(integerProperty.getMaximum).exists(_ < intValue)) {
        VerifyResult.error(s"'$intValue' has to be less than ${integerProperty.getMaximum}: $path")
      } else {
        VerifyResult.success
      }
    case _: IntegerProperty => VerifyResult.error(s"${value.getNodeType} should be an integer: $path")
    case longProperty: LongProperty if value.isNumber && value.canConvertToLong =>
      val longValue = value.asLong()
      if (Option(longProperty.getMinimum).exists(_ > longValue)) {
        VerifyResult.error(s"'$longValue' has to be greater than ${longProperty.getMinimum}: $path")
      } else if (Option(longProperty.getMaximum).exists(_ < longValue)) {
        VerifyResult.error(s"'$longValue' has to be less than ${longProperty.getMaximum}: $path")
      } else {
        VerifyResult.success
      }
    case _: LongProperty => VerifyResult.error(s"${value.getNodeType} should be a long: $path")
    case uuidProperty: UUIDProperty if value.isTextual =>
      if (Try(UUID.fromString(value.asText())).isFailure) {
        VerifyResult.error(s"'${value.asText()}' is not an uuid: $path")
      } else {
        VerifyResult.success
      }
    case uuidProperty: UUIDProperty => VerifyResult.error(s"${value.getNodeType} should be a uuid: $path")
    case _: BooleanProperty if value.isBoolean => VerifyResult.success
    case _: BooleanProperty => VerifyResult.error(s"${value.getNodeType} should be a boolean: $path")
    case _: DateProperty if value.isTextual =>
      if (Try(DateTimeFormatter.ISO_DATE.parse(value.asText())).isFailure) {
        VerifyResult.error(s"'${value.asText()}' should be a date: $path")
      } else {
        VerifyResult.success
      }
    case _: DateProperty => VerifyResult.error(s"${value.getNodeType} should be a date: $path")
    case _: DateTimeProperty if value.isTextual =>
      if (Try(DateTimeFormatter.ISO_INSTANT.parse(value.asText())).isFailure) {
        VerifyResult.error(s"'${value.asText()}' should be a datetime: $path")
      } else {
        VerifyResult.success
      }
    case _: DateTimeProperty => VerifyResult.error(s"${value.getNodeType} should be a datetime: $path")
    case refProperty: RefProperty =>
      Option(swagger.getDefinitions.get(refProperty.getSimpleRef)).map {
        model =>
          verifyNode(path + ".", value, model.getProperties.toMap)
      }.getOrElse(VerifyResult.error(s"Referenced model does not exists: ${refProperty.get$ref()}"))
  }
}

object SwaggerJsonVerifier {
  val emailPattern = "^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$".r

  val objectMapper = new ObjectMapper()
}