package controllers

import javax.inject._
import dto.{CreateEventRequest, EventResponse, UpdateEventRequest}
import errors.BadRequestException
import play.api.libs.json.{JsError, JsValue, Json, Reads}
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

  private def toResponse(event: Event): EventResponse =
    EventResponse(event.id, event.userId, event.amount, event.eventType, event.createdAt)

  private def newEvent(request: CreateEventRequest): Event =
    Event(
      id = 0,
      userId = request.userId,
      amount = request.amount,
      eventType = request.eventType,
      createdAt = Instant.now()
    )

  private def mergeUpdate(id: Int, existing: Event, request: UpdateEventRequest): Event =
    Event(
      id = id,
      userId = request.userId,
      amount = request.amount,
      eventType = request.eventType,
      createdAt = existing.createdAt
    )

  private def validateJson[A: Reads](body: JsValue): Future[A] =
    body
      .validate[A]
      .fold(
        errors => Future.failed(BadRequestException(s"Invalid JSON: ${JsError.toJson(errors)}")),
        Future.successful
      )

  def createEvent: Action[JsValue] = Action.async(parse.json) { request =>
    validateJson[CreateEventRequest](request.body).flatMap { createRequest =>
      val eventToCreate = newEvent(createRequest)

      service.createEvent(eventToCreate).map { createdId =>
        val createdEvent = eventToCreate.copy(id = createdId)
        Created(Json.toJson(toResponse(createdEvent)))
      }
    }
  }

  def getAllEvents: Action[AnyContent] = Action.async { _ =>
    service.getAllEvents.map { events =>
      Ok(Json.toJson(events.map(toResponse)))
    }
  }

  def getEventById(id: Int): Action[AnyContent] = Action.async { _ =>
    service.getEventById(id).map { e =>
      Ok(Json.toJson(toResponse(e)))
    }
  }

  def updateEvent(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    validateJson[UpdateEventRequest](request.body).flatMap { updateRequest =>
      service.getEventById(id).flatMap { existingEvent =>
        val updatedEvent = mergeUpdate(id, existingEvent, updateRequest)
        service.updateEvent(id, updatedEvent).map(_ => NoContent)
      }
    }
  }

  def deleteEvent(id: Int): Action[AnyContent] = Action.async { _ =>
    service.deleteEvent(id).map(_ => NoContent)
  }
}
