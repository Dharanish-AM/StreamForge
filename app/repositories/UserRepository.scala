package repositories

import javax.inject.{Inject, Singleton}
import models.{User, UserTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit
    ec: ExecutionContext
) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val users = TableQuery[UserTable]

  def list(): Future[Seq[User]] = db.run(users.result)

  def create(name: String, email: String): Future[User] = {
    val insertQuery =
      users returning users.map(_.id) into ((user, id) => user.copy(id = id))
    db.run(insertQuery += User(0L, name, email))
  }
}
