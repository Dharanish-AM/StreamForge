package repositories

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.Event
import tables.EventTable.events
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

@Singleton
class EventRepository @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext) {

  private val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val insertProjection = events.map(e => (e.userId, e.amount, e.eventType, e.createdAt))

  def create(event: Event): Future[Int] =
    db.run(
      (insertProjection returning events.map(_.id)) +=
        (event.userId, event.amount, event.eventType, event.createdAt)
    )

  def getAll: Future[Seq[Event]] =
    db.run(events.result)

  def getById(id: Int): Future[Option[Event]] =
    db.run(events.filter(_.id === id).result.headOption)

  def update(id: Int, updatedEvent: Event): Future[Int] =
    db.run(
      events
        .filter(_.id === id)
        .map(e => (e.userId, e.amount, e.eventType, e.createdAt))
        .update(
          (
            updatedEvent.userId,
            updatedEvent.amount,
            updatedEvent.eventType,
            updatedEvent.createdAt
          )
        )
    )

  def delete(id: Int): Future[Int] =
    db.run(events.filter(_.id === id).delete)

}
