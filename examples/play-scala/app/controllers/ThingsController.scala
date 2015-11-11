package controllers

import java.util.UUID
import javax.inject.Inject

import dal.ThingsRepository
import models.{Link, ThingsPage, ThingsPageLinks}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class ThingsController @Inject()(thingRepository: ThingsRepository)(implicit ec: ExecutionContext) extends Controller {
  def getPage(offset: Int, limit: Int) = Action.async {
    implicit request =>
      thingRepository.getPage(offset, limit).map {
        things =>

          val page = ThingsPage(things, ThingsPageLinks(Link.fromCall(routes.ThingsController.getPage(offset, limit)), None, None))
          Ok(Json.toJson(page))
      }
  }

  def createThing = Action {
    Created
  }

  def getThing(id: UUID) = Action.async {
    implicit request =>
      thingRepository.getById(id).map {
        case Some(thing) =>
          Ok(Json.toJson(thing))
        case None =>
          NotFound
      }
  }
}
