package controllers

import java.util.UUID
import javax.inject.Inject

import dal.ThingsRepository
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class ThingsController @Inject()(thingRepository: ThingsRepository)(implicit ec: ExecutionContext) extends Controller {
  def getPage(offset: Int, limit: Int) = Action.async {
    thingRepository.getPage(offset, limit).map {
      things =>
        Ok(Json.toJson(things))
    }
  }

  def getThing(id: UUID) = Action {
    Ok("bla")
  }
}
