package de.leanovate.swaggercheck.shrinkable

import com.fasterxml.jackson.core.JsonGenerator
import org.scalacheck.Shrink

import scala.collection.immutable.Stream._

/**
  * Json object.
  *
  * @param required optional set of required fields for shrinking
  * @param order optional order of fields
  * @param fields the fields of the object
  */
case class CheckJsObject(
                          required: Set[String],
                          order: Option[Seq[String]],
                          fields: Map[String, CheckJsValue]
                        ) extends CheckJsValue {
  override def generate(json: JsonGenerator): Unit = {
    json.writeStartObject()
    order match {
      case Some(fieldNames) => fieldNames.foreach {
        name =>
          fields.get(name).foreach {
            value =>
              json.writeFieldName(name)
              value.generate(json)
          }
      }
      case None =>
        fields.foreach {
          case (key, value) =>
            json.writeFieldName(key)
            value.generate(json)
        }
    }
    json.writeEndObject()
  }

  override def shrink: Stream[CheckJsObject] = {
    removeChunks(fields.keySet -- required).map {
      removeFields =>
        CheckJsObject(required, order, fields -- removeFields)
    }.append(shrinkOne(fields))
  }

  def removeChunks(names: Traversable[String]): Stream[Traversable[String]] = {
    if (names.isEmpty)
      empty
    else if (names.tail.isEmpty)
      Stream(names)
    else {
      val half = names.size / 2
      val left = names.take(half)
      val right = names.drop(half)

      cons(names, removeChunks(left).append(removeChunks(right)))
    }
  }

  private def shrinkOne(remaining: Map[String, CheckJsValue]): Stream[CheckJsObject] =
    if (remaining.isEmpty)
      empty
    else {
      val head = remaining.head
      val tail = remaining.tail

      val headShrink = Shrink.shrink[CheckJsValue](head._2).map(v => CheckJsObject(required, order, fields.updated(head._1, v)))

      headShrink.append(shrinkOne(tail))
    }
}

object CheckJsObject {
  def empty: CheckJsObject = CheckJsObject(Set.empty, None, Map.empty)

  /**
    * Create a fixed json object that will not shrink.
    */
  def fixed(fields: Seq[(String, CheckJsValue)]): CheckJsObject =
    CheckJsObject(fields.map(_._1).toSet, Some(fields.map(_._1)), fields.toMap)

  implicit lazy val shrinkJsValue: Shrink[CheckJsObject] = Shrink[CheckJsObject](_.shrink)
}
