package de.leanovate.swaggercheck.schema

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import de.leanovate.swaggercheck.shrinkable._
import de.leanovate.swaggercheck.{SwaggerChecks, VerifyResult}
import org.scalacheck.Gen

@JsonDeserialize(builder = classOf[SchemaObjectBuilder])
trait SchemaObject {
  def generate(context: SwaggerChecks): Gen[CheckJsValue]

  def verify(context: SwaggerChecks, path: Seq[String], node: CheckJsValue): VerifyResult
}

object SchemaObject {
  def arbitraryObj(ctx: SwaggerChecks): Gen[CheckJsValue] = for {
    size <- Gen.choose(0, ctx.maxItems)
    properties <- Gen.listOfN(size, arbitraryProperty)
  } yield CheckJsObject(Set.empty, None, properties.toMap)

  def arbitraryArray(ctx: SwaggerChecks): Gen[CheckJsValue] = for {
    size <- Gen.choose(0, ctx.maxItems)
    items <- Gen.listOfN(size, arbitraryValue)
  } yield CheckJsArray(None, items)

  def arbitraryValue: Gen[CheckJsValue] = Gen.oneOf(
    Gen.alphaStr.map(CheckJsString.unformatted),
    Gen.posNum[Int].map(CheckJsInteger(None, None, _)),
    Gen.oneOf(CheckJsBoolean(true), CheckJsBoolean(false))
  )

  def arbitraryProperty: Gen[(String, CheckJsValue)] = for {
    key <- Gen.identifier
    value <- arbitraryValue
  } yield key -> value
}
