package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

case class StringDefinition (
                         format: Option[String],
                         minLength: Option[Int],
                         maxLength: Option[Int],
                         pattern: Option[String],
                         enum: Option[Set[String]]
                       ) extends Definition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = {
    nodeAdapter.asString(node) match {
      case Some(value) =>
        if (minLength.exists(_ > value.length))
          ValidationResult.error(s"'$value' has to be at least ${minLength.mkString} chars long in path $path")
        else if (maxLength.exists(_ < value.length))
          ValidationResult.error(s"'$value' has to be at most ${maxLength.mkString} chars long in path $path")
        else if (pattern.exists(!_.r.pattern.matcher(value).matches()))
          ValidationResult.error(s"'$value' has match '${pattern.mkString}' in path $path")
        else if (enum.exists(e => e.nonEmpty && !e.contains(value)))
          ValidationResult.error(s"'$value' has to be one of ${enum.map(_.mkString(", ")).mkString} in path $path")
        else
          format.flatMap(schema.findStringFormat).map(_.validate(path, value)).getOrElse(ValidationResult.success)
      case _ =>
        ValidationResult.error(s"$node should be a string in path $path")
    }
  }
}
