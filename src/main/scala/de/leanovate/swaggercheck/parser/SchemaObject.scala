package de.leanovate.swaggercheck.parser

import com.fasterxml.jackson.annotation.JsonTypeInfo._
import com.fasterxml.jackson.annotation.{JsonProperty, JsonSubTypes, JsonTypeInfo, JsonTypeName}
import com.fasterxml.jackson.databind.JsonNode
import de.leanovate.swaggercheck.{Generators, SwaggerContext}
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
  def generate(context: SwaggerContext): Gen[JsonNode]
}

@JsonTypeName("object")
case class ObjectDefinition(
                             required: Set[String],
                             properties: Map[String, SchemaObject]
                             ) extends SchemaObject {
  override def generate(ctx: SwaggerContext): Gen[JsonNode] = {
    val propertyGens: Traversable[Gen[(String, JsonNode)]] = properties.map {
      case (name, schema) if required.contains(name) =>
        schema.generate(ctx).map(value => name -> value)
      case (name, schema) =>
        Gen.oneOf(
          Gen.const(ctx.nodeFactory.nullNode()),
          schema.generate(ctx)
        ).map(value => name -> value)
    }
    Gen.sequence(propertyGens).map(_.foldLeft(ctx.nodeFactory.objectNode()) {
      (result, element) =>
        result.set(element._1, element._2)
        result
    })
  }
}

@JsonTypeName("array")
case class ArrayDefinition(
                            minItems: Option[Int],
                            maxItems: Option[Int],
                            items: SchemaObject
                            ) extends SchemaObject {
  override def generate(ctx: SwaggerContext): Gen[JsonNode] = for {
    size <- Gen.choose(minItems.getOrElse(0), maxItems.getOrElse(10))
    items <- Gen.listOfN(size, items.generate(ctx))
  } yield items.foldLeft(ctx.nodeFactory.arrayNode()) {
      case (result, item) =>
        result.add(item)
    }
}

@JsonTypeName("string")
case class StringDefinition(
                             format: Option[String],
                             minLength: Option[Int],
                             maxLength: Option[Int],
                             pattern: Option[String],
                             enum: List[String]
                             ) extends SchemaObject {
  override def generate(ctx: SwaggerContext): Gen[JsonNode] = {
    val generator: Gen[String] = (enum, pattern, format) match {
      case (one :: Nil, _, _) => Gen.const(one)
      case (first :: second :: rest, _, _) => Gen.oneOf(first, second, rest: _ *)
      case (Nil, Some(regex), _) => Generators.regexMatch(regex)
      case (Nil, _, Some(formatName)) if ctx.stringFormats.contains(formatName) =>
        ctx.stringFormats(formatName).generate
      case _ => for {
        len <- Gen.choose(minLength.getOrElse(0), maxLength.getOrElse(255))
        chars <- Gen.listOfN(len, Gen.alphaNumChar)
      } yield chars.mkString
    }
    generator.map(ctx.nodeFactory.textNode)
  }
}

@JsonTypeName("integer")
case class IntegerDefinition(
                              format: Option[String],
                              minimum: Option[Long],
                              maximum: Option[Long]
                              ) extends SchemaObject {
  override def generate(ctx: SwaggerContext): Gen[JsonNode] = {
    val generator: Gen[Long] = format match {
      case Some(formatName) if ctx.integerFormats.contains(formatName) =>
        ctx.integerFormats(formatName).generate
      case _ => Gen.choose(minimum.getOrElse(Long.MinValue), maximum.getOrElse(Long.MaxValue))
    }
    generator.map(ctx.nodeFactory.numberNode)
  }
}

@JsonTypeName("number")
case class NumberDefinition(
                             format: Option[String],
                             minimum: Option[Double],
                             maximum: Option[Double]
                             ) extends SchemaObject {
  override def generate(ctx: SwaggerContext): Gen[JsonNode] = {
    val generator: Gen[Double] = format match {
      case Some(formatName) if ctx.numberFormats.contains(formatName) =>
        ctx.numberFormats(formatName).generate
      case _ => Gen.choose(minimum.getOrElse(Double.MinValue), maximum.getOrElse(Double.MaxValue))
    }
    generator.map(ctx.nodeFactory.numberNode)
  }
}

@JsonTypeName("boolean")
case class BooleanDefinition(

                              ) extends SchemaObject {
  override def generate(ctx: SwaggerContext): Gen[JsonNode] =
    Gen.oneOf(ctx.nodeFactory.booleanNode(true), ctx.nodeFactory.booleanNode(false))
}

case class ReferenceDefinition(
                                @JsonProperty("$ref")
                                ref: String
                                ) extends SchemaObject {
  def simpleRef: String = if (ref.startsWith("#/definitions/")) ref.substring(14) else ref

  override def generate(ctx: SwaggerContext): Gen[JsonNode] = {
    ctx.swaggerAPI.definitions.get(simpleRef)
      .map(_.generate(ctx))
    .getOrElse(throw new RuntimeException(s"Referenced model does not exists: $simpleRef"))
  }
}
