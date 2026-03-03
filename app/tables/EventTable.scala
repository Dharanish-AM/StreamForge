package tables

import models.Event
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag
import java.time.Instant

class EventTable(tag: Tag) extends Table[Event](tag, "events") {
  def id        = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userId    = column[Int]("user_id")
  def amount    = column[BigDecimal]("amount")
  def eventType = column[String]("event_type")
  def createdAt = column[Instant]("created_at")

  def * = (id, userId, amount, eventType, createdAt) <> ((Event.apply _).tupled, Event.unapply)
}

object EventTable {
  val events = TableQuery[EventTable]
}
