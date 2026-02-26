package models

import slick.jdbc.PostgresProfile.api._

case class User(id: Long, name: String, email: String)

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id    = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name  = column[String]("name")
  def email = column[String]("email")

  def * = (id, name, email) <> (User.tupled, User.unapply)
}
