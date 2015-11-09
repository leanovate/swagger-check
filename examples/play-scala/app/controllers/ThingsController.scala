package controllers

import javax.inject.Inject

import dal.ThingRepository
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class ThingsController @Inject()(thingRepository: ThingRepository)(implicit ec: ExecutionContext) extends Controller {
  def getPage(offset: Int, limit: Int) = Action.async {
    thingRepository.getPage(offset, limit).map {
      things =>
        Ok(Json.toJson(things))
    }
  }
}
