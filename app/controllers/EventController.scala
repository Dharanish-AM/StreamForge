package controllers

import javax.inject._
import dto.{CreateEventRequest, EventResponse, UpdateEventRequest}
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._
import play.api.Logging
import services.EventService

import scala.concurrent.{ExecutionContext, Future}
import models.Event
import java.time.Instant

@Singleton
class EventController @Inject() (
    cc: ControllerComponents,
    service: EventService
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with Logging {

  def createEvent: Action[JsValue] = Action.async(parse.json) { request =>
    logger.info("Received create event request")
    request.body.validate[CreateEventRequest].fold(
      errors => {
        logger.warn(s"Create event request failed validation: ${JsError.toJson(errors)}")
        Future.successful(
          BadRequest(Json.obj("message" -> s"Invalid JSON: ${JsError.toJson(errors)}"))
        )
      },
      createRequest => {
        logger.debug(
          s"Creating event for userId=${createRequest.userId}, eventType=${createRequest.eventType}"
        )
        val eventToCreate = Event(
          id = 0,
          userId = createRequest.userId,
          amount = createRequest.amount,
          eventType = createRequest.eventType,
          createdAt = Instant.now()
        )

        service.createEvent(eventToCreate).map { createdId =>
          logger.info(s"Created event id=$createdId")
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
        }.recover {
          case e: IllegalArgumentException =>
            logger.warn(s"Validation failed: ${e.getMessage}")
            BadRequest(Json.obj("message" -> e.getMessage))
        }
      }
    )
  }

  def getAllEvents: Action[AnyContent] = Action.async { _ =>
    logger.info("Received get all events request")
    service.getAllEvents.map { events =>
      logger.debug(s"Fetched ${events.size} events")
      Ok(
        Json.toJson(
          events.map(event =>
            EventResponse(event.id, event.userId, event.amount, event.eventType, event.createdAt)
          )
        )
      )
    }
  }

  def getKpiStats: Action[AnyContent] = Action.async { _ =>
    logger.info("Received get KPI stats request")
    service.getKpiStats.map { kpiStats =>
      logger.debug(
        s"KPI stats calculated: totalEvents=${kpiStats.totalEvents}, totalVolume=${kpiStats.totalVolume}, uniqueUsers=${kpiStats.uniqueUsers}"
      )
      Ok(Json.toJson(kpiStats))
    }
  }

  def getEventById(id: Int): Action[AnyContent] = Action.async { _ =>
    logger.info(s"Received get event by id request: id=$id")
    service.getEventById(id).map { e =>
      e match {
        case Some(event) =>
          logger.debug(s"Event found: id=$id")
          Ok(
            Json.toJson(
              EventResponse(event.id, event.userId, event.amount, event.eventType, event.createdAt)
            )
          )
        case None =>
          logger.warn(s"Event not found: id=$id")
          NotFound(Json.obj("message" -> s"Event with id $id not found"))
      }
    }
  }

  def updateEvent(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    logger.info(s"Received update event request: id=$id")
    request.body.validate[UpdateEventRequest].fold(
      errors => {
        logger.warn(s"Update event request failed validation for id=$id: ${JsError.toJson(errors)}")
        Future.successful(
          BadRequest(Json.obj("message" -> s"Invalid JSON: ${JsError.toJson(errors)}"))
        )
      },
      updateRequest => {
        service.getEventById(id).flatMap { existingEvent =>
          existingEvent match {
            case Some(event) =>
              logger.debug(s"Updating event id=$id")
              val updatedEvent = Event(
                id = id,
                userId = updateRequest.userId,
                amount = updateRequest.amount,
                eventType = updateRequest.eventType,
                createdAt = event.createdAt
              )

              service.updateEvent(id, updatedEvent).map { rowsUpdated =>
                if (rowsUpdated == 0) {
                  logger.warn(s"Event update affected 0 rows for id=$id")
                  NotFound(Json.obj("message" -> s"Event with id $id not found"))
                } else {
                  logger.info(s"Updated event id=$id")
                  NoContent
                }
              }.recover {
                case e: IllegalArgumentException =>
                  logger.warn(s"Validation failed for id=$id: ${e.getMessage}")
                  BadRequest(Json.obj("message" -> e.getMessage))
              }

            case None =>
              logger.warn(s"Cannot update non-existent event id=$id")
              Future.successful(NotFound(Json.obj("message" -> s"Event with id $id not found")))
          }
        }
      }
    )
  }

  def deleteEvent(id: Int): Action[AnyContent] = Action.async { _ =>
    logger.info(s"Received delete event request: id=$id")
    service.deleteEvent(id).map { rowsDeleted =>
      if (rowsDeleted == 0) {
        logger.warn(s"Event delete affected 0 rows for id=$id")
        NotFound(Json.obj("message" -> s"Event with id $id not found"))
      } else {
        logger.info(s"Deleted event id=$id")
        NoContent
      }
    }
  }
}
