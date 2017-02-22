package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

case class TestNode(
                     array: Option[Seq[TestNode]] = None,
                     boolean: Option[Boolean] = None,
                     integer: Option[BigInt] = None,
                     number: Option[BigDecimal] = None,
                     obj: Option[Map[String, TestNode]] = None,
                     string: Option[String] = None,
                     isNull: Boolean = false
                   )

object TestNode {
  implicit object Adapter extends NodeAdapter[TestNode] {
    override def asArray(node: TestNode): Option[Seq[TestNode]] = node.array

    override def asNumber(node: TestNode): Option[BigDecimal] = node.number

    override def asString(node: TestNode): Option[String] = node.string

    override def asBoolean(node: TestNode): Option[Boolean] = node.boolean

    override def asInteger(node: TestNode): Option[BigInt] = node.integer

    override def createNull: TestNode = TestNode(isNull = true)

    override def isNull(node: TestNode): Boolean = node.isNull

    override def asObject(node: TestNode): Option[Map[String, TestNode]] = node.obj
  }
}