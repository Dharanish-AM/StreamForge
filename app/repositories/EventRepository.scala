package repositories

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import models.Event
import tables.EventTable.events

@Singleton
class EventRepository @Inject() (db: Database)(implicit ec: ExecutionContext) {

  // insert event
  def create(event: Event): Future[Int] = {
    val insertQuery =
      (events.map(e => (e.userId, e.amount, e.eventType, e.createdAt)) returning events.map(
        _.id
      )) +=
        (event.userId, event.amount, event.eventType, event.createdAt)
    db.run(insertQuery)
  }

  // get all events
  def getAll: Future[Seq[Event]] = {
    val getAllQuery = events.result
    db.run(getAllQuery)
  }

  // get by id
  def getById(id: Int): Future[Option[Event]] = {
    val getByIdQuery = events.filter(_.id === id).result.headOption
    db.run(getByIdQuery)
  }

  // update by id
  def update(id: Int, updatedEvent: Event): Future[Int] = {
    val updateQuery = events
      .filter(_.id === id)
      .map(e => (e.userId, e.amount, e.eventType, e.createdAt))
      .update(
        (updatedEvent.userId, updatedEvent.amount, updatedEvent.eventType, updatedEvent.createdAt)
      )
    db.run(updateQuery)
  }

  // delete
  def delete(id: Int): Future[Int] = {
    val deleteQuery = events.filter(_.id === id).delete
    db.run(deleteQuery)
  }

}
