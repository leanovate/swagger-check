package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.generators.Generators
import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.model.{JsonPath, Schema, StringDefinition, ValidationResult}
import de.leanovate.swaggercheck.shrinkable.{CheckJsString, CheckJsValue}
import org.scalacheck.Gen

case class GeneratableString(
                              definition: StringDefinition
                            ) extends GeneratableDefinition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult[T] =
    definition.validate(schema, path, node)

  override def generate(schema: GeneratableSchema): Gen[CheckJsValue] = {
    (definition.enum, definition.pattern) match {
      case (Some(one :: Nil), _) => Gen.const(CheckJsString.formatted(one))
      case (Some(first :: second :: rest), _) => Gen.oneOf(first, second, rest: _ *).map(CheckJsString.formatted)
      case (_, Some(regex)) => Generators.regexMatch(regex).map(CheckJsString.formatted)
      case _ =>
        definition.format
          .flatMap(schema.findGeneratableStringFormat)
          .map(_.generate.map(CheckJsString.formatted))
          .getOrElse(
            for {
              len <- Gen.choose(definition.minLength.getOrElse(0), definition.maxLength.getOrElse(255))
              chars <- Gen.listOfN(len, Gen.alphaNumChar)
            } yield CheckJsString(formatted = false, definition.minLength, chars.mkString)
          )
    }
  }
}
