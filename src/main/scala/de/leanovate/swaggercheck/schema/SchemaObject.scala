package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.annotation.JsonTypeInfo._
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.core.util.JsonParserSequence
import com.fasterxml.jackson.core.{JsonParser, JsonToken}
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.util.TokenBuffer
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer, JsonNode}
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type", defaultImpl = classOf[ReferenceDefinition])
@JsonSubTypes(Array[JsonSubTypes.Type](
  new JsonSubTypes.Type(name = "object", value = classOf[ObjectDefinition]),
  new JsonSubTypes.Type(name = "array", value = classOf[ArrayDefinition]),
  new JsonSubTypes.Type(name = "string", value = classOf[StringDefinition]),
  new JsonSubTypes.Type(name = "integer", value = classOf[IntegerDefinition]),
  new JsonSubTypes.Type(name = "number", value = classOf[NumberDefinition]),
  new JsonSubTypes.Type(name = "boolean", value = classOf[BooleanDefinition])
))
trait SchemaObject {
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

//class SchemaObjectDeserializer extends JsonDeserializer[SchemaObject] {
//  override def deserialize(p: JsonParser, ctxt: DeserializationContext): SchemaObject = {
//    var jp = p
//    var t = jp.getCurrentToken()
//    if (t == JsonToken.START_OBJECT) {
//      t = jp.nextToken()
//    } else if (t == JsonToken.START_ARRAY) {
//      return EmptyDefinition
//    } else if (t != JsonToken.FIELD_NAME) {
//      return EmptyDefinition
//    }
//
//    var tb: TokenBuffer = null
//
//    while(t == JsonToken.FIELD_NAME) {
//      val name = jp.getCurrentName()
//      jp.nextToken()
//      if ( name == "type" ) {
//
//      }
//      if (tb eq null) {
//        tb = new TokenBuffer(null, false)
//      }
//      tb.writeFieldName(name)
//      tb.copyCurrentStructure(jp)
//      t = jp.nextToken()
//    }
//
//    if (tb ne null) {
//      jp = JsonParserSequence.createFlattened(tb.asParser(jp), jp)
//    }
//    jp.nextToken()
//
//    ctxt.findNonContextualValueDeserializer(ctxt.constructType())
//  }
//}