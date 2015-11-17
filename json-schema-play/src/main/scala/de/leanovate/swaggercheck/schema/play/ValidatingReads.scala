package de.leanovate.swaggercheck.schema.play

import de.leanovate.swaggercheck.schema.model._
import de.leanovate.swaggercheck.schema.play.JsValueNodeAdapter._
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsResult, JsValue, Reads}

object ValidatingReads {
  def validating[T](schema: DefaultSchema)(implicit underlying: Reads[T]): Reads[T] = new Reads[T] {
    override def reads(json: JsValue): JsResult[T] = schema.validate(json) match {
      case ValidationSuccess => underlying.reads(json)
      case ValidationFailure(failures) => JsError(ValidationError(failures))
    }
  }

  def validating[T](schema: Schema, definition: Definition)(implicit underlying: Reads[T]): Reads[T] = new Reads[T] {
    override def reads(json: JsValue): JsResult[T] = definition.validate(schema, JsonPath(), json) match {
      case ValidationSuccess => underlying.reads(json)
      case ValidationFailure(failures) => JsError(ValidationError(failures))
    }
  }
}
