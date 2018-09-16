package utils.database

import java.sql.SQLException

import cats.effect.IO
import doobie.implicits._
import doobie.util.fragment
import doobie.Transactor
import utils.errors.{Error, SqlError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import utils.Extensions._


object DBActions {

  type FragmentFunction = () => Either[Error, fragment.Fragment]
  type DBActionResult =         Either[Error, DBResponseStatus]

  def action(actionImpl: FragmentFunction)
            (implicit transactor: Transactor.Aux[IO, Unit]): Future[DBActionResult] = {

    actionImpl() match {
      case Left(e)          => Future.successful { Left(e) }
      case Right(fragment)  =>
        val actionResult:     Either[SQLException, Int]  => DBActionResult = (transactionResult: Either[SQLException, Int]) => {
          transactionResult match {
            case Left(error)      =>  Left(SqlError(error))
            case Right(result)    =>  Right(DBResponseStatus(result))
          }
        }

        fragment.update.run.transact(transactor).attemptSql.unsafeToFuture() attemptAction actionResult
    }
  }
}
