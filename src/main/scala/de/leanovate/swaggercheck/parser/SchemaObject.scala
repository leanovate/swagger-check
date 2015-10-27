package de.leanovate.swaggercheck.parser

import com.fasterxml.jackson.annotation.JsonTypeInfo._
import com.fasterxml.jackson.annotation.{JsonProperty, JsonSubTypes, JsonTypeInfo, JsonTypeName}
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import de.leanovate.swaggercheck.{Generators, SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

import scala.collection.JavaConversions._

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type", defaultImpl = classOf[ReferenceDefinition])
@JsonSubTypes(Array[JsonSubTypes.Type](
  new JsonSubTypes.Type(name = "object", value = classOf[ObjectDefinition]),
  new JsonSubTypes.Type(name = "array", value = classOf[ArrayDefinition]),
  new JsonSubTypes.Type(name = "string", value = classOf[StringDefinition]),
  new JsonSubTypes.Type(name = "integer", value = classOf[IntegerDefinition]),
  new JsonSubTypes.Type(name = "number", value = classOf[NumberDefinition]),
  new JsonSubTypes.Type(name = "boolean", value = classOf[BooleanDefinition])
))
sealed trait SchemaObject {
  def generate(context: SwaggerChecks): Gen[JsonNode]

  def verify(context: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult
}

object SchemaObject {
  val nodeFactory = JsonNodeFactory.instance

  def arbitraryObj: Gen[JsonNode] = for {
    size <- Gen.choose(0, 10)
    properties <- Gen.listOfN(size, arbitraryProperty)
  } yield
    properties.foldLeft(nodeFactory.objectNode()) {
      (result, prop) =>
        result.set(prop._1, prop._2)
        result
    }

  def arbitraryArray: Gen[JsonNode] = for {
    size <- Gen.choose(0, 10)
    items <- Gen.listOfN(size, arbitraryValue)
  } yield
    items.foldLeft(nodeFactory.arrayNode()) {
      (result, value) =>
        result.add(value)
    }

  def arbitraryValue: Gen[JsonNode] = Gen.oneOf(
    Gen.alphaStr.map(nodeFactory.textNode),
    Gen.posNum[Int].map(nodeFactory.numberNode),
    Gen.oneOf(nodeFactory.booleanNode(true), nodeFactory.booleanNode(false))
  )

  def arbitraryProperty: Gen[(String, JsonNode)] = for {
    key <- Gen.identifier
    value <- arbitraryValue
  } yield key -> value
}

@JsonTypeName("object")
case class ObjectDefinition(
                             required: Option[Set[String]],
                             properties: Option[Map[String, SchemaObject]]
                             ) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = {
    if (properties.isEmpty) {
      arbitraryObj
    } else {
      properties.map {
        props =>
          val propertyGens: Traversable[Gen[(String, JsonNode)]] = {
            props.map {
              case (name, schema) if required.exists(_.contains(name)) =>
                schema.generate(ctx).map(value => name -> value)
              case (name, schema) =>
                Gen.oneOf(
                  Gen.const(nodeFactory.nullNode()),
                  schema.generate(ctx)
                ).map(value => name -> value)
            }
          }
          Gen.sequence(propertyGens).map(_.foldLeft(nodeFactory.objectNode()) {
            (result, element) =>
              result.set(element._1, element._2)
              result
          })
      }.getOrElse(arbitraryObj)
    }
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    if (node.isObject) {
      properties.map {
        props =>
          props.foldLeft(VerifyResult.success) {
            case (result, (name, schema)) =>
              val field = Option(node.get(name)).getOrElse(nodeFactory.nullNode())
              if (!field.isNull || required.exists(_.contains(name)))
                result.combine(schema.verify(ctx, path :+ name, field))
              else
                VerifyResult.success
          }
      }.getOrElse(VerifyResult.success)
    } else {
      VerifyResult.error(s"${node.getNodeType} should be an object: ${path.mkString(".")}")
    }
  }
}

@JsonTypeName("array")
case class ArrayDefinition(
                            minItems: Option[Int],
                            maxItems: Option[Int],
                            items: Option[SchemaObject]
                            ) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = items.map {
    itemsSchema =>
      for {
        size <- Gen.choose(minItems.getOrElse(0), maxItems.getOrElse(10))
        elements <- Gen.listOfN(size, itemsSchema.generate(ctx))
      } yield elements.foldLeft(nodeFactory.arrayNode()) {
        case (result, element) =>
          result.add(element)
      }
  }.getOrElse(arbitraryArray)

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    if (node.isArray) {
      if (minItems.exists(_ > node.size()))
        VerifyResult.error(s"$node should have at least ${minItems.mkString} items: ${path.mkString(".")}")
      else if (maxItems.exists(_ < node.size()))
        VerifyResult.error(s"$node should have at least ${maxItems.mkString} items: ${path.mkString(".")}")
      else
        items.map {
          itemsSchema =>
            Range(0, node.size()).foldLeft(VerifyResult.success) {
              (result, index) =>
                val element = node.get(index)
                result.combine(itemsSchema.verify(ctx, (path.mkString(".") + s"[$index]") :: Nil, element))
            }
        }.getOrElse(VerifyResult.success)
    } else {
      VerifyResult.error(s"${node.getNodeType} should be an array: ${path.mkString(".")}")
    }
  }
}

@JsonTypeName("string")
case class StringDefinition(
                             format: Option[String],
                             minLength: Option[Int],
                             maxLength: Option[Int],
                             pattern: Option[String],
                             enum: Option[List[String]]
                             ) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = {
    val generator: Gen[String] = (enum, pattern, format) match {
      case (Some(one :: Nil), _, _) => Gen.const(one)
      case (Some(first :: second :: rest), _, _) => Gen.oneOf(first, second, rest: _ *)
      case (_, Some(regex), _) => Generators.regexMatch(regex)
      case (_, _, Some(formatName)) if ctx.stringFormats.contains(formatName) =>
        ctx.stringFormats(formatName).generate
      case _ => for {
        len <- Gen.choose(minLength.getOrElse(0), maxLength.getOrElse(255))
        chars <- Gen.listOfN(len, Gen.alphaNumChar)
      } yield chars.mkString
    }
    generator.map(nodeFactory.textNode)
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    if (node.isTextual) {
      val value = node.asText()
      if (minLength.exists(_ > value.length))
        VerifyResult.error(s"'$value' has to be at least ${minLength.mkString} chars long: ${path.mkString(".")}")
      else if (maxLength.exists(_ < value.length))
        VerifyResult.error(s"'$value' has to be at most ${maxLength.mkString} chars long: ${path.mkString(".")}")
      else if (pattern.exists(!_.r.pattern.matcher(value).matches()))
        VerifyResult.error(s"'$value' has match '${pattern.mkString}': ${path.mkString(".")}")
      else if (enum.exists(e => e.nonEmpty && !e.contains(value)))
        VerifyResult.error(s"'$value' has to be one of ${enum.map(_.mkString(", ")).mkString}: ${path.mkString(".")}")
      else
        format.flatMap(ctx.stringFormats.get).map(_.verify(path.mkString("."), value)).getOrElse(VerifyResult.success)
    } else {
      VerifyResult.error(s"${node.getNodeType} should be a string: ${path.mkString(".")}")
    }
  }
}

@JsonTypeName("integer")
case class IntegerDefinition(
                              format: Option[String],
                              minimum: Option[Long],
                              maximum: Option[Long]
                              ) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = {
    val generator: Gen[Long] = format match {
      case Some(formatName) if ctx.integerFormats.contains(formatName) =>
        ctx.integerFormats(formatName).generate
      case _ => Gen.choose(minimum.getOrElse(Long.MinValue), maximum.getOrElse(Long.MaxValue))
    }
    generator.map(nodeFactory.numberNode)
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    if (node.isNumber && node.canConvertToLong) {
      val value = node.asLong()
      if (minimum.exists(_ > value))
        VerifyResult.error(s"'$value' has to be greater than ${minimum.mkString}: ${path.mkString(".")}")
      else if (maximum.exists(_ < value))
        VerifyResult.error(s"'$value' has to be less than ${maximum.mkString}: ${path.mkString(".")}")
      else
        format.flatMap(ctx.integerFormats.get).map(_.verify(path.mkString("."), value)).getOrElse(VerifyResult.success)
    } else {
      VerifyResult.error(s"${node.getNodeType} should be a long: $path")
    }
  }
}

@JsonTypeName("number")
case class NumberDefinition(
                             format: Option[String],
                             minimum: Option[Double],
                             maximum: Option[Double]
                             ) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = {
    val generator: Gen[Double] = format match {
      case Some(formatName) if ctx.numberFormats.contains(formatName) =>
        ctx.numberFormats(formatName).generate
      case _ => Gen.choose(minimum.getOrElse(Double.MinValue), maximum.getOrElse(Double.MaxValue))
    }
    generator.map(nodeFactory.numberNode)
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    if (node.isNumber) {
      val value = node.asDouble()
      if (minimum.exists(_ > value))
        VerifyResult.error(s"'$value' has to be greater than ${minimum.mkString}: ${path.mkString(".")}")
      else if (maximum.exists(_ < value))
        VerifyResult.error(s"'$value' has to be less than ${maximum.mkString}: ${path.mkString(".")}")
      else
        format.flatMap(ctx.numberFormats.get).map(_.verify(path.mkString("."), value)).getOrElse(VerifyResult.success)
    } else {
      VerifyResult.error(s"${node.getNodeType} should be a long: $path")
    }
  }
}

@JsonTypeName("boolean")
case class BooleanDefinition(

                              ) extends SchemaObject {

  import SchemaObject._

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] =
    Gen.oneOf(nodeFactory.booleanNode(true), nodeFactory.booleanNode(false))

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    if (node.isBoolean) {
      VerifyResult.success
    } else {
      VerifyResult.error(s"${node.getNodeType} should be a boolean: ${path.mkString(".")}")
    }
  }
}

case class ReferenceDefinition(
                                @JsonProperty("$ref")
                                ref: String
                                ) extends SchemaObject {
  def simpleRef: String = if (ref.startsWith("#/definitions/")) ref.substring(14) else ref

  override def generate(ctx: SwaggerChecks): Gen[JsonNode] = {
    ctx.swaggerAPI.definitions.get(simpleRef)
      .map(_.generate(ctx))
      .getOrElse(throw new RuntimeException(s"Referenced model does not exists: $simpleRef"))
  }

  override def verify(ctx: SwaggerChecks, path: Seq[String], node: JsonNode): VerifyResult = {
    ctx.swaggerAPI.definitions.get(simpleRef)
      .map(_.verify(ctx, path, node))
      .getOrElse(throw new RuntimeException(s"Referenced model does not exists: $simpleRef"))
  }
}
