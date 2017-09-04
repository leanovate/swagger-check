package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

/**
  * Element of a json schema model.
  */
case class Parameter(name: String, in:String, required: Boolean, definition: Definition) extends Definition {

  override def validate[T](schema: Schema, path: JsonPath, node: T)(implicit nodeAdapter: NodeAdapter[T]): ValidationResult = definition.validate(schema, path, node)
}

object Parameter {
  def build(
             name: String,
             in: String,
             required: Option[Boolean],
             schema: Option[Definition],
             schemaType: Option[String],
             format: Option[String],
             allowEmptyValue: Option[Boolean],
             items: Option[Definition],
             maximum: Option[BigDecimal],
             exclusiveMaximum: Option[Boolean],
             minimum: Option[BigDecimal],
             exclusiveMinimum: Option[Boolean],
             maxLength: Option[Int],
             minLength: Option[Int],
             pattern: Option[String],
             maxItems: Option[Int],
             minItems: Option[Int],
             uniqueItems: Option[Boolean],
             enum: Option[List[String]]
           ): Parameter = {
    if (in == "body") Parameter(name, in, required.getOrElse(false), schema.get)
    else schemaType match {
      case Some("array") => Parameter(name, in, required.getOrElse(false), ArrayDefinition(minItems, maxItems, items))
      case Some("string") => Parameter(name, in, required.getOrElse(false), StringDefinition(format, minLength, maxLength, pattern, enum))
      case Some("integer") => Parameter(name, in, required.getOrElse(false), IntegerDefinition(format, minimum.map(_.longValue()), maximum.map(_.longValue())))
      case Some("number") => Parameter(name, in, required.getOrElse(false), NumberDefinition(format, minimum.map(_.doubleValue()), maximum.map(_.doubleValue())))
      case Some("boolean") => Parameter(name, in, required.getOrElse(false), BooleanDefinition)
      case _ => throw new RuntimeException(s"Unkown schemaType: $schemaType")
    }
  }
}