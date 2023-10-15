package domain.tables

import domain.models.User
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime

class UserTable(tag: Tag) extends Table[User](tag, Some("scala"), "users") {

  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.Unique)

  def email = column[String]("email", O.Unique)

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name")

  def password = column[String]("password")

  def role = column[String]("role")

  def birthDate = column[LocalDateTime]("birth_date")

  def address = column[Option[String]]("address")

  def phoneNumber = column[Option[String]]("phone_number")

  /**
   * This is the table's default "projection".
   * It defines how the columns are converted to and from the User object.
   */
  def * = (id, email, firstName, lastName, password, role, birthDate, address, phoneNumber) <> ((User.apply _).tupled, User.unapply)

}
