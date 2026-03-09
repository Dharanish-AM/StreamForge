package controllers

import javax.inject._
import dto.{CreateEventRequest, EventResponse, UpdateEventRequest}
import play.api.libs.json.{JsError, JsValue, Json}
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
    request.body.validate[CreateEventRequest].fold(
      errors =>
        Future.successful(
          BadRequest(Json.obj("message" -> s"Invalid JSON: ${JsError.toJson(errors)}"))
        ),
      createRequest => {
        val eventToCreate = Event(
          id = 0,
          userId = createRequest.userId,
          amount = createRequest.amount,
          eventType = createRequest.eventType,
          createdAt = Instant.now()
        )

        service.createEvent(eventToCreate).map { createdId =>
          val createdEvent = eventToCreate.copy(id = createdId)
          Created(
            Json.toJson(
              EventResponse(
                createdEvent.id,
                createdEvent.userId,
                createdEvent.amount,
                createdEvent.eventType,
                createdEvent.createdAt
              )
            )
          )
        }
      }
    )
  }

  def getAllEvents: Action[AnyContent] = Action.async { _ =>
    service.getAllEvents.map { events =>
      Ok(
        Json.toJson(
          events.map(event =>
            EventResponse(event.id, event.userId, event.amount, event.eventType, event.createdAt)
          )
        )
      )
    }
  }

  def getEventById(id: Int): Action[AnyContent] = Action.async { _ =>
    service.getEventById(id).map { e =>
      e match {
        case Some(event) =>
          Ok(
            Json.toJson(
              EventResponse(event.id, event.userId, event.amount, event.eventType, event.createdAt)
            )
          )
        case None => NotFound(Json.obj("message" -> s"Event with id $id not found"))
      }
    }
  }

  def updateEvent(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[UpdateEventRequest].fold(
      errors =>
        Future.successful(
          BadRequest(Json.obj("message" -> s"Invalid JSON: ${JsError.toJson(errors)}"))
        ),
      updateRequest => {
        service.getEventById(id).flatMap { existingEvent =>
          existingEvent match {
            case Some(event) =>
              val updatedEvent = Event(
                id = id,
                userId = updateRequest.userId,
                amount = updateRequest.amount,
                eventType = updateRequest.eventType,
                createdAt = event.createdAt
              )

              service.updateEvent(id, updatedEvent).map { rowsUpdated =>
                if (rowsUpdated == 0) {
                  NotFound(Json.obj("message" -> s"Event with id $id not found"))
                } else {
                  NoContent
                }
              }

            case None =>
              Future.successful(NotFound(Json.obj("message" -> s"Event with id $id not found")))
          }
        }
      }
    )
  }

  def deleteEvent(id: Int): Action[AnyContent] = Action.async { _ =>
    service.deleteEvent(id).map { rowsDeleted =>
      if (rowsDeleted == 0) {
        NotFound(Json.obj("message" -> s"Event with id $id not found"))
      } else {
        NoContent
      }
    }
  }
}
