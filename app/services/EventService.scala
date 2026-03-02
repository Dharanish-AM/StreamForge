package services

import repositories.EventRepository
import scala.concurrent.Future
import models.Event
import javax.inject.Singleton
import javax.inject.Inject

@Singleton
class EventService @Inject() (eventRepository: EventRepository) {

  def createEvent(event: Event): Future[Int] =
    eventRepository.create(event)

  def getAllEvents: Future[Seq[Event]] =
    eventRepository.getAll

  def getEventById(id: Int): Future[Option[Event]] =
    eventRepository.getById(id)

  def updateEvent(id: Int, updatedEvent: Event): Future[Int] =
    eventRepository.update(id, updatedEvent)

  def deleteEvent(id: Int): Future[Int] =
    eventRepository.delete(id)
}
