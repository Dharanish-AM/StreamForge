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

  def createEvent(event: Event): Future[Int] =
    eventRepository.create(event)

  def getAllEvents: Future[Seq[Event]] =
    eventRepository.getAll

  def getEventById(id: Int): Future[Event] =
    eventRepository.getById(id).map { event =>
      event.getOrElse(throw NotFoundException(s"Event with id $id not found"))
    }

  def updateEvent(id: Int, updatedEvent: Event): Future[Int] =
    eventRepository.update(id, updatedEvent).map { rowsUpdated =>
      if (rowsUpdated == 0) {
        throw NotFoundException(s"Event with id $id not found")
      }
      rowsUpdated
    }

  def deleteEvent(id: Int): Future[Int] =
    eventRepository.delete(id).map { rowsDeleted =>
      if (rowsDeleted == 0) {
        throw NotFoundException(s"Event with id $id not found")
      }
      rowsDeleted
    }
}
