package de.leanovate.swaggercheck.schema.model

case class JsonPath(path: String) {
  def field(name: String) = if (path.isEmpty)
    copy(path = name)
  else
    copy(path = "$path.$name")

  def index(idx: Int) = copy(path = s"$path.[$idx]")

  override def toString = path
}