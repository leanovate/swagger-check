package dal

import models.{Thing, Person}

import scala.concurrent.Future

class ThingRepository {
  def list() : Future[Seq[Person]] = ???

  def create(name: String, age: Int) : Future[Person]= ???

  def getPage(offset:Int, limit: Int) : Future[Seq[Thing]] = ???
}
