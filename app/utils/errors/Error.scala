package utils.errors

import java.sql.SQLException

import play.api.mvc.Results._

trait Error {
  val errorType: ErrorType
  val status: Status
  def message: String
}

abstract class ErrorType(val errorType: String)

case object ThrowableError  extends ErrorType(errorType = "thrown_error")
final case class ThrowableError(
  throwable:  Throwable,
  errorType:  ErrorType = ThrowableError,
  status:     Status    = InternalServerError
)  extends Error {
  def message: String = s"MESSAGE: ${throwable.toString}, STATUS: $status, Error Type: $errorType"
}

case object DBError         extends ErrorType(errorType = "db_error")
final case class SqlError(
  sql:        SQLException,
  errorType:  ErrorType = DBError,
  status:     Status    = InternalServerError
) extends Error {
  def message: String = s"MESSAGE: ${sql.getMessage}, STATUS: $status, Error Type: $errorType"
}

case object NotFoundError   extends ErrorType(errorType = "not_found")
final case class NotFound(
  msg:        String,
  errorType:  ErrorType = NotFoundError,
  status:     Status    = play.api.mvc.Results.NotFound
) extends Error {
  def message: String = s"MESSAGE: $msg STATUS: $status, Error Type: $errorType"
}

final case class ValueExists(
  msg:        String,
  errorType:  ErrorType,
  status:     Status    = BadRequest
) extends Error {
  def message: String = s"MESSAGE: $msg STATUS: $status, Error Type: $errorType"
}

final case class Failed(
  msg:        String,
  errorType:  ErrorType,
  status:     Status    = InternalServerError
) extends Error {
  def message: String = s"$msg status: $status, Error Type: $errorType"
}

object ErrorToHttpStatus {
  def getStatus(error: Error): Status = {
    error.status
  }
}