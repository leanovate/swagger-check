package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.gen.formats.GeneratableFormat
import de.leanovate.swaggercheck.schema.model.Schema
import de.leanovate.swaggercheck.schema.model.formats.ValueFormat
import de.leanovate.swaggercheck.shrinkable._
import org.scalacheck.Gen

trait GeneratableSchema extends Schema {
  /**
    * Modify max items.
    */
  def maxItems: Int

  /**
    * Modify max items.
    */
  def withMaxItems(newMaxItems: Int): GeneratableSchema

  /**
    * Create a child context with reduced maxItems.
    *
    * Mostly used internally to ensure that size of arrays decay with depth.
    */
  def childContext: GeneratableSchema = withMaxItems(maxItems / 2)

  /**
    * Generator for arbitrary json objects.
    */
  def arbitraryObj: Gen[CheckJsValue] = for {
    size <- Gen.choose(0, maxItems)
    properties <- Gen.listOfN(size, arbitraryProperty)
  } yield CheckJsObject(Set.empty, None, properties.toMap)

  /**
    * Generator for arbitrary json arrays.
    */
  def arbitraryArray: Gen[CheckJsValue] = for {
    size <- Gen.choose(0, maxItems)
    items <- Gen.listOfN(size, arbitraryValue)
  } yield CheckJsArray(None, items)

  /**
    * Generator for arbitrary json values.
    */
  def arbitraryValue: Gen[CheckJsValue] = Gen.oneOf(
    Gen.alphaStr.map(CheckJsString.unformatted),
    Gen.posNum[Int].map(CheckJsInteger(None, None, _)),
    Gen.oneOf(CheckJsBoolean(true), CheckJsBoolean(false))
  )

  def arbitraryProperty: Gen[(String, CheckJsValue)] = for {
    key <- Gen.identifier
    value <- arbitraryValue
  } yield key -> value

  def findGeneratableIntegerFormat(format: String): Option[GeneratableFormat[BigInt]]

  def findGeneratableNumberFormat(format: String): Option[GeneratableFormat[BigDecimal]]

  def findGeneratableStringFormat(format: String): Option[GeneratableFormat[String]]

  override def findStringFormat(name: String): Option[ValueFormat[String]] = findGeneratableStringFormat(name)

  override def findIntegerFormat(name: String): Option[ValueFormat[BigInt]] = findGeneratableIntegerFormat(name)

  override def findNumberFormat(name: String): Option[ValueFormat[BigDecimal]] = findGeneratableNumberFormat(name)
}
