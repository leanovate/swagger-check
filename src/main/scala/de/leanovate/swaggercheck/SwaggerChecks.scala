package de.leanovate.swaggercheck

import java.io.{InputStream, File, FileInputStream}

import com.fasterxml.jackson.databind.ObjectMapper
import de.leanovate.swaggercheck.formats.{Format, IntegerFormats, NumberFormats, StringFormats}
import de.leanovate.swaggercheck.schema.{SchemaObject, SwaggerAPI}
import org.scalacheck.Gen
import com.fasterxml.jackson.databind.JsonNode

case class SwaggerChecks(
                          swaggerAPI: SwaggerAPI,
                          stringFormats: Map[String, Format[String]] = StringFormats.defaultFormats,
                          integerFormats: Map[String, Format[Long]] = IntegerFormats.defaultFormats,
                          numberFormats: Map[String, Format[Double]] = NumberFormats.defaultFormats
                          ) {
  def jsonGenerator(name: String): Gen[String] =
    swaggerAPI.definitions.get(name)
      .map(_.generate(this).map(_.toString))
      .getOrElse(throw new RuntimeException(s"Swagger does not contain a model $name"))

  def jsonVerifier(name: String): Verifier[String] =
    swaggerAPI.definitions.get(name)
      .map(schemaVerifier)
      .getOrElse(throw new RuntimeException(s"Swagger does not contain a model $name"))

  def withStringFormats(formats: (String, Format[String])*) =
    copy(stringFormats = stringFormats ++ Map(formats: _*))

  def withIntegerFormats(formats: (String, Format[Long])*) =
    copy(integerFormats = integerFormats ++ Map(formats: _*))

  def withNumberFormats(formats: (String, Format[Double])*) =
    copy(numberFormats = numberFormats ++ Map(formats: _*))

  private def schemaVerifier(schemaObject: SchemaObject): Verifier[String] = new Verifier[String] {
    override def verify(value: String): VerifyResult = {
      val tree = new ObjectMapper().readTree(value)

      schemaObject.verify(SwaggerChecks.this, Nil, tree)
    }
  }
}

object SwaggerChecks {
  def apply(swaggerAsString: String): SwaggerChecks =
    new SwaggerChecks(SwaggerAPI.parse(swaggerAsString))

  def apply(swaggerInput: InputStream): SwaggerChecks =
    new SwaggerChecks(SwaggerAPI.parse(swaggerInput))

  def apply(swaggerFile: File): SwaggerChecks =
    apply(new FileInputStream(swaggerFile))
}
