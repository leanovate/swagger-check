package de.leanovate.swaggercheck

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import io.swagger.models.Model
import io.swagger.models.properties._
import org.scalacheck.Gen

import scala.collection.JavaConversions._

object GenSwaggerJson {
  val nodeFactory = JsonNodeFactory.instance

  def modelJsonGen(model: Model): Gen[String] =
    modelNodeGen(model).map(_.toString)

  def modelNodeGen(model: Model): Gen[JsonNode] =
    objectNodeGen(model.getProperties.toMap)

  def objectNodeGen(properties: Map[String, Property]): Gen[JsonNode] = {
    val propertyGens: Traversable[Gen[(String, JsonNode)]] = properties.map {
      case (name, property) =>
        propertyNodeGen(property).map(value => name -> value)
    }
    Gen.sequence(propertyGens).map(_.foldLeft(nodeFactory.objectNode()) {
      (result, element) =>
        result.set(element._1, element._2)
        result
    })
  }

  def propertyNodeGen(property: Property): Gen[JsonNode] =
    if (property.getRequired) {
      requiredPropertyNodeGen(property)
    } else {
      Gen.oneOf(
        Gen.const(nodeFactory.nullNode()),
        requiredPropertyNodeGen(property)
      )
    }

  def requiredPropertyNodeGen(property: Property): Gen[JsonNode] = property match {
    case objectProperty: ObjectProperty =>
      objectNodeGen(objectProperty.getProperties.toMap)
    case arrayProperty: ArrayProperty =>
      Gen.choose(0, 20).flatMap(Gen.listOfN(_, propertyNodeGen(arrayProperty.getItems)))
        .map(_.foldLeft(nodeFactory.arrayNode()) {
        (result, value) =>
          result.add(value)
      })
    case emailProperty: EmailProperty =>
      Generators.email.map(nodeFactory.textNode)
    case stringProperty: StringProperty =>
      val minLen = Option(stringProperty.getMinLength).map(_.toInt).getOrElse(0)
      val maxLen = Option(stringProperty.getMaxLength).map(_.toInt).getOrElse(255)
      val generator: Gen[List[Char]] = Option(stringProperty.getPattern)
        .map(pattern => new GenRegexMatch().regexGenerator(pattern))
        .getOrElse(Gen.chooseNum[Int](minLen, maxLen).flatMap(Gen.listOfN(_, Gen.alphaNumChar)))
      generator.map(chars => nodeFactory.textNode(chars.mkString))
    case decimalProperty: DecimalProperty =>
      val min = Option(decimalProperty.getMinimum).map(_.toDouble).getOrElse(Double.MinValue)
      val max = Option(decimalProperty.getMaximum).map(_.toDouble).getOrElse(Double.MaxValue)
      Gen.chooseNum[Double](min, max).map(nodeFactory.numberNode)
    case integerProperty: IntegerProperty =>
      val min = Option(integerProperty.getMinimum).map(_.toInt).getOrElse(Int.MinValue)
      val max = Option(integerProperty.getMaximum).map(_.toInt).getOrElse(Int.MaxValue)
      Gen.chooseNum[Int](min, max).map(nodeFactory.numberNode)
    case longProperty: LongProperty =>
      val min = Option(longProperty.getMinimum).map(_.toLong).getOrElse(Long.MinValue)
      val max = Option(longProperty.getMaximum).map(_.toLong).getOrElse(Long.MaxValue)
      Gen.chooseNum[Long](min, max).map(nodeFactory.numberNode)
    case uuidProperty: UUIDProperty =>
      Gen.uuid.map(_.toString).map(nodeFactory.textNode)
  }
}
