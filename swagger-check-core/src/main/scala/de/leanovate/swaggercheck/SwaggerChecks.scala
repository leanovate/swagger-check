package de.leanovate.swaggercheck

import java.io.{File, FileInputStream, InputStream}

import de.leanovate.swaggercheck.schema.gen.GeneratableDefinition._
import de.leanovate.swaggercheck.schema.gen.GeneratableSchema
import de.leanovate.swaggercheck.schema.gen.formats.{GeneratableFormat, GeneratableIntegerFormats, GeneratableNumberFormats, GeneratableStringFormats}
import de.leanovate.swaggercheck.schema.model.{Definition, JsonPath, ValidationResult}
import de.leanovate.swaggercheck.schema.{Operation, SwaggerAPI}
import de.leanovate.swaggercheck.shrinkable.CheckJsValue
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
                          stringFormats: Map[String, GeneratableFormat[String]] = GeneratableStringFormats.defaultFormats,
                          integerFormats: Map[String, GeneratableFormat[BigInt]] = GeneratableIntegerFormats.defaultFormats,
                          numberFormats: Map[String, GeneratableFormat[BigDecimal]] = GeneratableNumberFormats.defaultFormats,
                          maxItems: Int = 10,
                          randomAdditionalFields : Boolean = false
                        ) extends GeneratableSchema {
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
  def jsonVerifier(name: String): Validator[String] =
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
                         (implicit responseExtractor: ResponseExtractor[R]): Validator[R] = {
    swaggerAPI.paths.get(path).flatMap {
      methods =>
        methods.get(method.toUpperCase).map(verifierForOperation[R])
    }.getOrElse(failingVerifier(s"No operation for method=$method path=$path"))
  }

  /**
    * Combines `requestGenerator` and `responseVerifier`.
    *
    * Convenient way to generate arbitrary requests together with a verifier of their expected responses.
    *
    * @param pathFilter Optional filter for paths (exact match)
    *
    * @tparam R request class
    * @tparam U response class
    */
  def operationVerifier[R, U](pathFilter: String => Boolean = _ => true)
                             (implicit requestBuilder: RequestCreator[R], responseExtractor: ResponseExtractor[U]): Gen[OperationValidator[R, U]] = {
    val operations = swaggerAPI.paths.filterKeys(pathFilter)

    for {
      (path, methods) <- Gen.oneOf(operations.toSeq)
      (method, operation) <- Gen.oneOf(methods.toSeq)
      request <- operation.generateRequest(this, method, path)
    } yield OperationValidator[R, U](request, verifierForOperation[U](operation))
  }

  /**
    * Add a self-defined string format.
    */
  def withStringFormats(formats: (String, GeneratableFormat[String])*) =
    copy(stringFormats = stringFormats ++ Map(formats: _*))

  /**
    * Add a self-defined integer format.
    */
  def withIntegerFormats(formats: (String, GeneratableFormat[BigInt])*) =
    copy(integerFormats = integerFormats ++ Map(formats: _*))

  /**
    * Add a self-defined number format.
    */
  def withNumberFormats(formats: (String, GeneratableFormat[BigDecimal])*) =
    copy(numberFormats = numberFormats ++ Map(formats: _*))

  /**
    * Modify max items.
    */
  def withMaxItems(newMaxItems: Int): SwaggerChecks = copy(maxItems = newMaxItems)

  def withRandomAdditionalFields() = copy(randomAdditionalFields = true)

  /**
    * Create a child context with reduced maxItems.
    *
    * Mostly used internally to ensure that size of arrays decay with depth.
    */
  override def childContext: SwaggerChecks = withMaxItems(maxItems / 2)

  private def verifierForSchema(expectedSchema: Definition): Validator[String] = new Validator[String] {
    override def verify(value: String): ValidationResult = {
      expectedSchema.validate(SwaggerChecks.this, JsonPath(), CheckJsValue.parse(value))
    }
  }

  private def verifierForOperation[R](operation: Operation)
                                     (implicit responseExtractor: ResponseExtractor[R]) = new Validator[R] {
    override def verify(value: R): ValidationResult = {
      val status = responseExtractor.status(value)

      operation.responses.get(status.toString).orElse(operation.responses.get("default"))
        .map(_.verify(SwaggerChecks.this, responseExtractor.headers(value), responseExtractor.body(value)))
        .getOrElse(ValidationResult.error(s"Invalid status=$status"))
    }
  }

  private def failingVerifier[T](failure: String) = new Validator[T] {
    override def verify(value: T): ValidationResult = ValidationResult.error(failure)
  }

  override def findGeneratableIntegerFormat(format: String): Option[GeneratableFormat[BigInt]] =
    integerFormats.get(format)

  override def findGeneratableStringFormat(format: String): Option[GeneratableFormat[String]] =
    stringFormats.get(format)

  override def findGeneratableNumberFormat(format: String): Option[GeneratableFormat[BigDecimal]] =
    numberFormats.get(format)

  override def findByRef(ref: String): Option[Definition] = {
    val simpleRef = if (ref.startsWith("#/definitions/")) ref.substring(14) else ref

    swaggerAPI.definitions.get(simpleRef)
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
