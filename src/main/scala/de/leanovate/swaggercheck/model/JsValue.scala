package de.leanovate.swaggercheck.model

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.core.{JsonFactory, JsonGenerator}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.scalacheck.util.Pretty

import scala.language.implicitConversions

@JsonDeserialize(using = classOf[JsValueDeserializer])
trait JsValue {

  import JsValue._

  def generate(json: JsonGenerator): Unit

  def minified: String = {
    val sw = new java.io.StringWriter
    val gen = jsonFactory.createGenerator(sw)

    generate(gen)

    gen.flush()
    sw.flush()
    sw.getBuffer.toString
  }

  def prettyfied: String = {
    val sw = new java.io.StringWriter
    val gen = jsonFactory.createGenerator(sw).setPrettyPrinter(
      new DefaultPrettyPrinter()
    )

    generate(gen)

    gen.flush()
    sw.flush()
    sw.getBuffer.toString
  }

  override def toString = minified
}

object JsValue {
  val jsonFactory = new JsonFactory()

  implicit def prettyJsValue(jsValue: JsValue): Pretty = Pretty { p => jsValue.prettyfied }
}