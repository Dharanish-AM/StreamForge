package repositories

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.Event
import dto.KpiResponse
import tables.EventTable.events
import play.api.db.slick.DatabaseConfigProvider
import play.api.Logging
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import scala.util.{Failure, Success}

@Singleton
class EventRepository @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
  extends Logging {

  private val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val insertProjection = events.map(e => (e.userId, e.amount, e.eventType, e.createdAt))

  private def runQuery[T](operation: String)(action: DBIO[T]): Future[T] = {
    val start = System.currentTimeMillis()
    logger.debug(s"Starting repository operation: $operation")
    db.run(action).andThen {
      case Success(_) =>
        logger.debug(
          s"Completed repository operation: $operation in ${System.currentTimeMillis() - start} ms"
        )
      case Failure(exception) =>
        logger.error(s"Repository operation failed: $operation", exception)
    }
  }

  def create(event: Event): Future[Int] =
    runQuery(s"create userId=${event.userId}, eventType=${event.eventType}")(
      (insertProjection returning events.map(_.id)) +=
        (event.userId, event.amount, event.eventType, event.createdAt)
    )

  def getAll: Future[Seq[Event]] =
    runQuery("getAll")(events.result)

  def getById(id: Int): Future[Option[Event]] =
    runQuery(s"getById id=$id")(events.filter(_.id === id).result.headOption)

  def update(id: Int, updatedEvent: Event): Future[Int] =
    runQuery(s"update id=$id")(
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
    runQuery(s"delete id=$id")(events.filter(_.id === id).delete)

  def getKpiStats: Future[KpiResponse] = {
    val aggregateAction = for {
      totalEvents <- events.length.result
      totalVolume <- events.map(_.amount).sum.result
      uniqueUsers <- events.map(_.userId).distinct.length.result
    } yield {
      val safeTotalVolume = totalVolume.getOrElse(BigDecimal(0))
      val averageAmount =
        if (totalEvents > 0) safeTotalVolume / BigDecimal(totalEvents) else BigDecimal(0)

      KpiResponse(
        totalEvents = totalEvents,
        totalVolume = safeTotalVolume,
        averageAmount = averageAmount,
        uniqueUsers = uniqueUsers
      )
    }

    runQuery("getKpiStats")(aggregateAction)
  }

}
