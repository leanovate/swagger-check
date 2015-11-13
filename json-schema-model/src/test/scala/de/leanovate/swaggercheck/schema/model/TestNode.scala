package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

case class TestNode(
                     array: Option[Seq[TestNode]] = None,
                     boolean: Option[Boolean] = None,
                     integer: Option[BigInt] = None
                   )

object TestNode {
  implicit val adapter = new NodeAdapter[TestNode] {
    override def asArray(node: TestNode): Option[Seq[TestNode]] = node.array

    override def asNumber(node: TestNode): Option[BigDecimal] = ???

    override def asString(node: TestNode): Option[String] = ???

    override def asBoolean(node: TestNode): Option[Boolean] = node.boolean

    override def asInteger(node: TestNode): Option[BigInt] = node.integer

    override def createNull: TestNode = ???

    override def isNull(node: TestNode): Boolean = ???

    override def asObject(node: TestNode): Option[Map[String, TestNode]] = ???
  }
}