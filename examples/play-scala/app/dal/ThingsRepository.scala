package dal

import models.{Thing, Person}

import scala.concurrent.Future

class ThingsRepository {
  def list() : Future[Seq[Thing]] = ???

  def create(name: String, age: Int) : Future[Thing]= ???

  def getPage(offset:Int, limit: Int) : Future[Seq[Thing]] = ???
}
