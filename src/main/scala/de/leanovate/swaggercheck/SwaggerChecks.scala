package de.leanovate.swaggercheck

import java.io.InputStream

import com.fasterxml.jackson.databind.ObjectMapper
import de.leanovate.swaggercheck.parser.{SchemaObject, SwaggerAPI}
import org.scalacheck.Gen

class SwaggerChecks(swaggerAPI: SwaggerAPI) {
  val context = SwaggerContext(swaggerAPI)

  def jsonGenerator(name: String): Gen[String] =
    swaggerAPI.definitions.get(name)
      .map(_.generate(context).map(_.toString))
      .getOrElse(throw new RuntimeException(s"Swagger does not contain a model $name"))

  def jsonVerifier(name: String): Verifier[String] =
    swaggerAPI.definitions.get(name)
      .map(schemaVerifier)
      .getOrElse(throw new RuntimeException(s"Swagger does not contain a model $name"))

  private def schemaVerifier(schemaObject: SchemaObject): Verifier[String] = new Verifier[String] {
    override def verify(value: String): VerifyResult = {
      val tree = new ObjectMapper().readTree(value)

      schemaObject.verify(context, Nil, tree)
    }
  }
}

object SwaggerChecks {
  def apply(swaggerAsString: String): SwaggerChecks =
    new SwaggerChecks(SwaggerAPI.parse(swaggerAsString))

  def apply(swaggerInput: InputStream): SwaggerChecks =
    new SwaggerChecks(SwaggerAPI.parse(swaggerInput))
}