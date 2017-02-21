package de.leanovate.swaggercheck.shrinkable

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.core.{JsonTokenId, JsonParser, JsonFactory, JsonGenerator}
import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import org.scalacheck.Shrink
import org.scalacheck.util.Pretty

import scala.annotation.{switch, tailrec}
import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions

trait CheckJsValue {

  import CheckJsValue._

  def asText(default: String): String = default

  def isNull : Boolean = false

  def shrink: Stream[CheckJsValue]

  def generate(json: JsonGenerator): Unit

  def minified: String = {
    val sw = new java.io.StringWriter
    val gen = jsonFactory.createGenerator(sw)

    generate(gen)

    gen.flush()
    sw.flush()
    sw.getBuffer.toString
  }

  def prettyfied: String = {
    val sw = new java.io.StringWriter
    val gen = jsonFactory.createGenerator(sw).setPrettyPrinter(
      new DefaultPrettyPrinter()
    )

    generate(gen)

    gen.flush()
    sw.flush()
    sw.getBuffer.toString
  }
}

object CheckJsValue {
  val jsonFactory = new JsonFactory()

  def parse(json: String) = deserialize(jsonFactory.createParser(json), EmptyState)

  implicit lazy val shrinkJsValue: Shrink[CheckJsValue] = Shrink[CheckJsValue](_.shrink)

  implicit def prettyJsValue(jsValue: CheckJsValue): Pretty = Pretty {
    p =>
      if (p.verbosity == 0)
        jsValue.minified
      else
        jsValue.prettyfied
  }

  implicit val adapter = new NodeAdapter[CheckJsValue] {
    override def asArray(node: CheckJsValue): Option[Seq[CheckJsValue]] = node match {
      case CheckJsArray(_, elements) => Some(elements)
      case _ => None
    }

    override def asNumber(node: CheckJsValue): Option[BigDecimal] = node match {
      case CheckJsNumber(_, _, value) => Some(value)
      case CheckJsInteger(_, _, value) => Some(BigDecimal(value))
      case _ => None
    }

    override def asString(node: CheckJsValue): Option[String] = node match {
      case CheckJsString(_, _, value) => Some(value)
      case _ => None
    }

    override def asBoolean(node: CheckJsValue): Option[Boolean] = node match {
      case CheckJsBoolean(value) => Some(value)
      case _ => None
    }

    override def asInteger(node: CheckJsValue): Option[BigInt] = node match {
      case CheckJsInteger(_, _, value) => Some(value)
      case _ => None
    }

    override def createNull: CheckJsValue = CheckJsNull

    override def isNull(node: CheckJsValue): Boolean = node == CheckJsNull

    override def asObject(node: CheckJsValue): Option[Map[String, CheckJsValue]] = node match {
      case CheckJsObject(_, _, fields) => Some(fields)
      case _ => None
    }
  }

  @tailrec
  private def deserialize(jp: JsonParser, current: State): CheckJsValue = {
    if (jp.getCurrentToken == null) {
      jp.nextToken()
    }

    val (optValue, next) = (jp.getCurrentToken.id(): @switch) match {
      case JsonTokenId.ID_NUMBER_INT => (Some(CheckJsInteger.fixed(jp.getBigIntegerValue)), current)
      case JsonTokenId.ID_NUMBER_FLOAT => (Some(CheckJsNumber.fixed(jp.getDecimalValue)), current)
      case JsonTokenId.ID_STRING => (Some(CheckJsString.formatted(jp.getText)), current)
      case JsonTokenId.ID_TRUE => (Some(CheckJsBoolean(true)), current)
      case JsonTokenId.ID_FALSE => (Some(CheckJsBoolean(false)), current)
      case JsonTokenId.ID_NULL => (Some(CheckJsNull), current)
      case JsonTokenId.ID_START_ARRAY => (None, current.startArray())
      case JsonTokenId.ID_END_ARRAY => (Some(current.asJsArray), current.parent)
      case JsonTokenId.ID_START_OBJECT => (None, current.startObject())
      case JsonTokenId.ID_FIELD_NAME => (None, current.setField(jp.getCurrentName))
      case JsonTokenId.ID_END_OBJECT => (Some(current.asJsObject), current.parent)
      case token =>
        throw new RuntimeException(s"Unsupported token: $token")
    }

    jp.nextToken()

    optValue match {
      case Some(v) if next.isEmpty => v
      case _ =>
        optValue.foreach(next.addValue)
        deserialize(jp, next)
    }
  }

  sealed trait State {
    def parent: State

    def isEmpty: Boolean

    def addValue(value: CheckJsValue): State

    def setField(fieldName: String): State = {
      throw new RuntimeException("We should have been reading object, something got wrong")
    }

    def asJsArray: CheckJsArray = {
      throw new RuntimeException("We should have been reading list, something got wrong")
    }

    def asJsObject: CheckJsObject = {
      throw new RuntimeException("We should have been reading object, something got wrong")
    }

    def startArray(): State = new InArrayState(this)

    def startObject(): State = new InObjectState(this)
  }

  object EmptyState extends State {
    override def parent: State = {
      throw new RuntimeException("We should have been value, something got wrong")
    }

    override def addValue(value: CheckJsValue): State = {
      throw new RuntimeException("We should have been value, something got wrong")
    }

    override val isEmpty: Boolean = true
  }

  class InArrayState(val parent: State, content: ListBuffer[CheckJsValue] = ListBuffer.empty) extends State {
    override def addValue(value: CheckJsValue): State = {
      content.append(value)
      this
    }

    override def asJsArray: CheckJsArray = CheckJsArray.fixed(content.to[List])
    override val isEmpty: Boolean = false
  }

  class InObjectState(val parent: State, content: ListBuffer[(String, CheckJsValue)] = ListBuffer.empty) extends State {
    var currentField: Option[String] = None

    override def addValue(value: CheckJsValue): State = currentField match {
      case Some(fieldName) =>
        content.append(fieldName -> value)
        currentField = None
        this
      case _ =>
        throw new RuntimeException("We should have been reading object, something got wrong (no field name)")
    }

    override def setField(fieldName: String): State = currentField match {
      case Some(_) =>
        throw new RuntimeException("We should have been reading object, something got wrong (duplicate field name)")
      case _ =>
        currentField = Some(fieldName)
        this
    }

    override def asJsObject: CheckJsObject = CheckJsObject.fixed(content)

    override val isEmpty: Boolean = false
  }

}