package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.{JsonParser, JsonTokenId}
import com.fasterxml.jackson.databind._

import scala.annotation.{tailrec, switch}
import scala.collection.mutable.ListBuffer

class JsValueDeserializer extends JsonDeserializer[JsValue] {

  import JsValueDeserializer._

  override def isCachable: Boolean = true

  override def getNullValue: JsValue = JsNull

  override def deserialize(jp: JsonParser, ctx: DeserializationContext): JsValue = {
    deserialize(jp, ctx, Nil)
  }

  @tailrec
  private def deserialize(jp: JsonParser, ctx: DeserializationContext, current: List[Context]): JsValue = {
    if (jp.getCurrentToken == null) {
      jp.nextToken()
    }

    val (optValue, next) = (jp.getCurrentToken.id(): @switch) match {
      case JsonTokenId.ID_NUMBER_INT => (Some(JsInteger.fixed(jp.getBigIntegerValue)), current)
      case JsonTokenId.ID_NUMBER_FLOAT => (Some(JsNumber.fixed(jp.getDecimalValue)), current)
      case JsonTokenId.ID_STRING => (Some(JsFormattedString(jp.getText)), current)
      case JsonTokenId.ID_TRUE => (Some(JsTrue), current)
      case JsonTokenId.ID_FALSE => (Some(JsFalse), current)
      case JsonTokenId.ID_NULL => (Some(JsNull), current)
      case JsonTokenId.ID_START_ARRAY => (None, new ReadList() :: current)
      case JsonTokenId.ID_END_ARRAY => (current.headOption.map(_.asJsArray), current.tail)
      case JsonTokenId.ID_START_OBJECT => (None, new ReadObject() :: current)
      case JsonTokenId.ID_FIELD_NAME =>
        current.headOption.foreach(_.setField(jp.getCurrentName))
        (None, current)
      case JsonTokenId.ID_END_OBJECT => (current.headOption.map(_.asJsObject), current.tail)
      case token =>
        throw new RuntimeException(s"Unsupported token: $token")
    }

    jp.nextToken()

    optValue match {
      case Some(v) if next.isEmpty => v

      case _ =>
        val toPass = optValue.map {
          v =>
            val head :: stack = next
            (head.addValue(v)) :: stack
        }.getOrElse(next)

        deserialize(jp, ctx, toPass)
    }
  }
}

object JsValueDeserializer {

  sealed trait Context {
    def addValue(value: JsValue): Context

    def setField(fieldName: String): Context = {
      throw new RuntimeException("We should have been reading object, something got wrong")
    }

    def asJsArray: JsArray = {
      throw new RuntimeException("We should have been reading list, something got wrong")
    }

    def asJsObject: JsObject = {
      throw new RuntimeException("We should have been reading object, something got wrong")
    }
  }

  class ReadList(content: ListBuffer[JsValue] = ListBuffer.empty) extends Context {
    override def addValue(value: JsValue): Context = {
      content.append(value)
      this
    }

    override def asJsArray: JsArray = JsArray.fixed(content.toSeq)
  }

  class ReadObject(content: ListBuffer[(String, JsValue)] = ListBuffer.empty) extends Context {
    var currentField: Option[String] = None

    override def addValue(value: JsValue): Context = currentField match {
      case Some(fieldName) =>
        content.append(fieldName -> value)
        currentField = None
        this
      case _ =>
        throw new RuntimeException("We should have been reading object, something got wrong (no field name)")
    }

    override def setField(fieldName: String): Context = currentField match {
      case Some(fieldName) =>
        throw new RuntimeException("We should have been reading object, something got wrong (duplicate field name)")
      case _ =>
        currentField = Some(fieldName)
        this
    }

    override def asJsObject: JsObject = JsObject.fixed(content.toMap)
  }

}