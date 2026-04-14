package services

import repositories.EventRepository
import scala.concurrent.Future
import models.Event
import dto.KpiResponse
import javax.inject.Singleton
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import play.api.Logging
import scala.util.{Failure, Success}

@Singleton
class EventService @Inject() (eventRepository: EventRepository)(implicit ec: ExecutionContext)
    extends Logging {

  private def trace[T](operation: String)(f: => Future[T]): Future[T] = {
    val start = System.currentTimeMillis()
    logger.debug(s"Starting service operation: $operation")
    f.andThen {
      case Success(_) =>
        logger.debug(
          s"Completed service operation: $operation in ${System.currentTimeMillis() - start} ms"
        )
      case Failure(exception) =>
        logger.error(s"Service operation failed: $operation", exception)
    }
  }

  def createEvent(event: Event): Future[Int] =
    trace(s"createEvent userId=${event.userId}, eventType=${event.eventType}") {
      eventRepository.create(event)
    }

  def getAllEvents: Future[Seq[Event]] =
    trace("getAllEvents") {
      eventRepository.getAll
    }

  def getEventById(id: Int): Future[Option[Event]] =
    trace(s"getEventById id=$id") {
      eventRepository.getById(id)
    }

  def updateEvent(id: Int, updatedEvent: Event): Future[Int] =
    trace(s"updateEvent id=$id") {
      eventRepository.update(id, updatedEvent)
    }

  def deleteEvent(id: Int): Future[Int] =
    trace(s"deleteEvent id=$id") {
      eventRepository.delete(id)
    }

  def getKpiStats: Future[KpiResponse] =
    trace("getKpiStats") {
      eventRepository.getKpiStats
    }
}
