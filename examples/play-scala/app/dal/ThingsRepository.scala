package dal

import java.util.UUID

import models.{Thing, ThingType}

import scala.concurrent.Future

class ThingsRepository {
  def list(): Future[Seq[Thing]] = ???

  def create(name: String, age: Int): Future[Thing] = ???

  def getPage(thingType: Option[ThingType.Type], offset: Int, limit: Int): Future[Seq[Thing]] = ???

  def getById(id: UUID): Future[Option[Thing]] = ???
}
