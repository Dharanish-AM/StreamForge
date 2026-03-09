package controllers

import javax.inject._
import dto.{CreateEventRequest, EventResponse, UpdateEventRequest}
import errors.BadRequestException
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
      errors => Future.failed(BadRequestException(s"Invalid JSON: ${JsError.toJson(errors)}")),
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
      Ok(Json.toJson(EventResponse(e.id, e.userId, e.amount, e.eventType, e.createdAt)))
    }
  }

  def updateEvent(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[UpdateEventRequest].fold(
      errors => Future.failed(BadRequestException(s"Invalid JSON: ${JsError.toJson(errors)}")),
      updateRequest => {
        service.getEventById(id).flatMap { existingEvent =>
          val updatedEvent = Event(
            id = id,
            userId = updateRequest.userId,
            amount = updateRequest.amount,
            eventType = updateRequest.eventType,
            createdAt = existingEvent.createdAt
          )

          service.updateEvent(id, updatedEvent).map(_ => NoContent)
        }
      }
    )
  }

  def deleteEvent(id: Int): Action[AnyContent] = Action.async { _ =>
    service.deleteEvent(id).map(_ => NoContent)
  }
}
