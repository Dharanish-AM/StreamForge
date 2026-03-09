package controllers

import javax.inject._
import dto.{CreateEventRequest, EventResponse, UpdateEventRequest}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.EventService

import scala.concurrent.{ExecutionContext, Future}
import models.Event
import java.time.Instant

@Singleton
class EventController @Inject() (
    cc: ControllerComponents,
    service: EventService
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def createEvent: Action[JsValue] = Action.async(parse.json) { request =>
    request.body
      .validate[CreateEventRequest]
      .fold(
        errors => Future.successful(BadRequest("Invalid JSON: " + errors.toString)),
        createRequest => {
          val event = Event(
            id = 0,
            userId = createRequest.userId,
            amount = createRequest.amount,
            eventType = createRequest.eventType,
            createdAt = Instant.now()
          )
          service
            .createEvent(event)
            .map(createdId =>
              Created(
                Json.toJson(
                  EventResponse(
                    createdId,
                    event.userId,
                    event.amount,
                    event.eventType,
                    event.createdAt
                  )
                )
              )
            )
        }
      )
  }

  def getAllEvents: Action[AnyContent] = Action.async { request =>
    service.getAllEvents.map { events =>
      Ok(
        Json.toJson(
          events.map(e => EventResponse(e.id, e.userId, e.amount, e.eventType, e.createdAt))
        )
      )
    }
  }

  def getEventById(id: Int): Action[AnyContent] = Action.async { request =>
    service.getEventById(id).map {
      case Some(e) =>
        Ok(Json.toJson(EventResponse(e.id, e.userId, e.amount, e.eventType, e.createdAt)))
      case None => NotFound(s"Event with id $id not found")
    }
  }

  def updateEvent(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    request.body
      .validate[UpdateEventRequest]
      .fold(
        errors => Future.successful(BadRequest("Invalid JSON: " + errors.toString)),
        updateRequest =>
          service.getEventById(id).flatMap {
            case None => Future.successful(NotFound(s"Event with id $id not found"))
            case Some(existingEvent) =>
              val updatedEvent = Event(
                id = id,
                userId = updateRequest.userId,
                amount = updateRequest.amount,
                eventType = updateRequest.eventType,
                createdAt = existingEvent.createdAt
              )

              service
                .updateEvent(id, updatedEvent)
                .map {
                  case 0 => NotFound(s"Event with id $id not found")
                  case _ => NoContent
                }
          }
      )
  }

  def deleteEvent(id: Int): Action[AnyContent] = Action.async { request =>
    service.deleteEvent(id).map {
      case 0 => NotFound(s"Event with id $id not found")
      case _ => NoContent
    }
  }
}
