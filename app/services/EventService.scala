package services

import errors.NotFoundException
import repositories.EventRepository
import scala.concurrent.Future
import models.Event
import javax.inject.Singleton
import javax.inject.Inject
import scala.concurrent.ExecutionContext

@Singleton
class EventService @Inject() (eventRepository: EventRepository)(implicit ec: ExecutionContext) {

  private def notFoundMessage(id: Int): String = s"Event with id $id not found"

  private def requireFound(event: Option[Event], id: Int): Event =
    event.getOrElse(throw NotFoundException(notFoundMessage(id)))

  private def requireAffectedRowCount(rows: Int, id: Int): Int = {
    if (rows == 0) {
      throw NotFoundException(notFoundMessage(id))
    }
    rows
  }

  def createEvent(event: Event): Future[Int] =
    eventRepository.create(event)

  def getAllEvents: Future[Seq[Event]] =
    eventRepository.getAll

  def getEventById(id: Int): Future[Event] =
    eventRepository.getById(id).map(event => requireFound(event, id))

  def updateEvent(id: Int, updatedEvent: Event): Future[Int] =
    eventRepository.update(id, updatedEvent).map(rowsUpdated => requireAffectedRowCount(rowsUpdated, id))

  def deleteEvent(id: Int): Future[Int] =
    eventRepository.delete(id).map(rowsDeleted => requireAffectedRowCount(rowsDeleted, id))
}
