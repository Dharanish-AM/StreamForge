package controllers

import javax.inject._
import play.api.libs.json.JsValue
import play.api.mvc._
import services.EventService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventController @Inject() (
    cc: ControllerComponents,
    service: EventService
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def createEvent: Action[JsValue] = Action.async(parse.json) { request =>
    // TODO: parse JSON and call service.createEvent(...)
    val json:JsValue = request.body
    println(json)
    Future.successful(Ok("Event created")) // Placeholder response
  }

  def getAllEvents: Action[AnyContent] = Action.async { request =>
    // TODO: call service.getAllEvents
    ???
  }

  def getEventById(id: Int): Action[AnyContent] = Action.async { request =>
    // TODO: call service.getEventById(id)
    ???
  }

  def updateEvent(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    // TODO: parse JSON and call service.updateEvent(id, ...)
    ???
  }

  def deleteEvent(id: Int): Action[AnyContent] = Action.async { request =>
    // TODO: call service.deleteEvent(id)
    ???
  }
}
