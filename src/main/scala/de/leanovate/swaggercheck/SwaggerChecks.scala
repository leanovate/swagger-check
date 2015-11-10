package de.leanovate.swaggercheck

import java.io.{File, FileInputStream, InputStream}

import de.leanovate.swaggercheck.formats.{Format, IntegerFormats, NumberFormats, StringFormats}
import de.leanovate.swaggercheck.model.CheckJsValue
import de.leanovate.swaggercheck.schema.{Operation, SchemaObject, SwaggerAPI}
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
                          integerFormats: Map[String, Format[BigInt]] = IntegerFormats.defaultFormats,
                          numberFormats: Map[String, Format[BigDecimal]] = NumberFormats.defaultFormats,
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
  def jsonGenerator(name: String): Gen[CheckJsValue] =
    swaggerAPI.definitions.get(name)
      .map(_.generate(this))
      .getOrElse(throw new RuntimeException(s"Swagger does not contain a model $name"))

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
      .map(verifierForSchema)
      .getOrElse(throw new RuntimeException(s"Swagger does not contain a model $name"))

  /**
    * Create a generator for requests based on a swagger file.
    *
    * @param pathFilter Optional filter for paths (exact match)
    * @param requestBuilder implicit creator for request instances
    * @tparam R request class (depends on the web framework, in play FakeRequest might be desired)
    * @return generator for requests
    */
  def requestGenerator[R](pathFilter: String => Boolean = _ => true)(implicit requestBuilder: RequestCreator[R]): Gen[R] = {
    val operations = swaggerAPI.paths.filterKeys(pathFilter)

    for {
      (path, methods) <- Gen.oneOf(operations.toSeq)
      (method, operation) <- Gen.oneOf(methods.toSeq)
      request <- operation.generateRequest(this, method, path)
    } yield request
  }

  /**
    * Create response verifier.
    *
    * @param method method of request
    * @param path path of request
    * @param responseExtractor implicit extractor for response instances
    * @tparam R response class (depends on the web framework)
    * @return
    */
  def responseVerifier[R](method: String, path: String)
                         (implicit responseExtractor: ResponseExtractor[R]): Verifier[R] = {
    swaggerAPI.paths.get(path).flatMap {
      methods =>
        methods.get(method.toUpperCase).map(verifierForOperation[R])
    }.getOrElse(failingVerifier(s"No operation for method=$method path=$path"))
  }

  /**
    * Add a self-defined string format.
    */
  def withStringFormats(formats: (String, Format[String])*) =
    copy(stringFormats = stringFormats ++ Map(formats: _*))

  /**
    * Add a self-defined integer format.
    */
  def withIntegerFormats(formats: (String, Format[BigInt])*) =
    copy(integerFormats = integerFormats ++ Map(formats: _*))

  /**
    * Add a self-defined number format.
    */
  def withNumberFormats(formats: (String, Format[BigDecimal])*) =
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

  private def verifierForSchema(expectedSchema: SchemaObject): Verifier[String] = new Verifier[String] {
    override def verify(value: String): VerifyResult = {
      expectedSchema.verify(SwaggerChecks.this, Nil, CheckJsValue.parse(value))
    }
  }

  private def verifierForOperation[R](operation: Operation)
                                     (implicit responseExtractor: ResponseExtractor[R]) = new Verifier[R] {
    override def verify(value: R): VerifyResult = {
      val status = responseExtractor.status(value)

      operation.responses.get(status.toString).orElse(operation.responses.get("default"))
        .map(_.verify(SwaggerChecks.this, responseExtractor.headers(value), responseExtractor.body(value)))
        .getOrElse(VerifyResult.error(s"Invalid status=$status"))
    }
  }

  private def failingVerifier[T](failure: String) = new Verifier[T] {
    override def verify(value: T): VerifyResult = VerifyResult.error(failure)
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
