package de.leanovate.swaggercheck

import java.io.{File, FileInputStream, InputStream}

import com.fasterxml.jackson.databind.ObjectMapper
import de.leanovate.swaggercheck.formats.{Format, IntegerFormats, NumberFormats, StringFormats}
import de.leanovate.swaggercheck.schema.{SchemaObject, SwaggerAPI}
import org.scalacheck.Gen

/**
 * Facade for all swagger related generators and verifiers.
 *
 * @param swaggerAPI the swagger API
 * @param stringFormats map of all string formats
 * @param integerFormats map of all integer formats
 * @param numberFormats map of all number formats
 * @param maxItems default maximum number of items in arrays (or objects with additionalProperties)
 *                 might be overriden be minItems or maxItems in swagger file
 */
case class SwaggerChecks(
                          swaggerAPI: SwaggerAPI,
                          stringFormats: Map[String, Format[String]] = StringFormats.defaultFormats,
                          integerFormats: Map[String, Format[Long]] = IntegerFormats.defaultFormats,
                          numberFormats: Map[String, Format[Double]] = NumberFormats.defaultFormats,
                          maxItems: Int = 10
                          ) {
  /**
   * Create a generator for random json based on a swagger definition.
   *
   * Usually you want to use this to ensure that all your deserializers are working.
   *
   * @param name name of the swagger definition
   * @return generator for json
   */
  def jsonGenerator(name: String): Gen[String] =
    swaggerAPI.definitions.get(name)
      .map(_.generate(this).map(_.toString))
      .getOrElse(throw new RuntimeException(s"Swagger does not contain a model $name"))

  /**
   * Create a generator for requests based on a swagger file.
   *
   * @param matchingPath Optional filter for paths (exact match)
   * @param requestBuilder implicit creator for request instances
   * @tparam R request class (depends on the web framework, in play FakeRequest might be desired)
   * @return generator for requests
   */
  def requestGenerator[R](matchingPath: Option[String])(implicit requestBuilder: RequestCreator[R]): Gen[R] = {
    val operations = swaggerAPI.paths.getOrElse(Map.empty).filterKeys(path => matchingPath.isEmpty || matchingPath.contains(path))

    for {
      (path, methods) <- Gen.oneOf(operations.toSeq)
      (method, operation) <- Gen.oneOf(methods.toSeq)
      request <- operation.generateRequest(this, method, path)
    } yield request
  }

  /**
   * Get a verifier that verifies, if a string contains a json that matches to a swagger definition.
   *
   * Usually you want to use this to ensure that all your serializers are working.
   *
   * @param name name of the swagger definition to check
   * @return a string verifier
   */
  def jsonVerifier(name: String): Verifier[String] =
    swaggerAPI.definitions.get(name)
      .map(schemaVerifier)
      .getOrElse(throw new RuntimeException(s"Swagger does not contain a model $name"))

  /**
   * Add a self-defined string format.
   */
  def withStringFormats(formats: (String, Format[String])*) =
    copy(stringFormats = stringFormats ++ Map(formats: _*))

  /**
   * Add a self-defined integer format.
   */
  def withIntegerFormats(formats: (String, Format[Long])*) =
    copy(integerFormats = integerFormats ++ Map(formats: _*))

  /**
   * Add a self-defined number format.
   */
  def withNumberFormats(formats: (String, Format[Double])*) =
    copy(numberFormats = numberFormats ++ Map(formats: _*))

  /**
   * Modify max items.
   */
  def withMaxItems(newMaxItems: Int): SwaggerChecks = copy(maxItems = newMaxItems)

  /**
   * Create a child context with reduced maxItems.
   *
   * Mostly used internally to ensure that size of arrays decay with depth.
   */
  def childContext: SwaggerChecks = withMaxItems(maxItems / 2)

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
