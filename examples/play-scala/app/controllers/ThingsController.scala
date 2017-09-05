package controllers

import java.util.UUID
import javax.inject.Inject

import dal.ThingsRepository
import models.{Link, ThingType, ThingsPage, ThingsPageLinks}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

class ThingsController @Inject()(
    thingRepository: ThingsRepository,
    components: ControllerComponents)(implicit ec: ExecutionContext)
    extends AbstractController(components) {
  def getPage(thingType: Option[ThingType.Type], offset: Int, limit: Int) =
    Action.async { implicit request =>
      thingRepository.getPage(thingType, offset, limit).map { things =>
        val page = ThingsPage(
          things,
          ThingsPageLinks(
            Link.fromCall(
              routes.ThingsController.getPage(thingType, offset, limit)),
            None,
            None))
        Ok(Json.toJson(page))
      }
    }

  def createThing = Action {
    Created
  }

  def getThing(id: UUID) = Action.async { implicit request =>
    thingRepository.getById(id).map {
      case Some(thing) =>
        Ok(Json.toJson(thing))
      case None =>
        NotFound
    }
  }
}
