package repositories

import cats.effect.IO
import doobie.util.transactor.Transactor
import services.repositories.TransactionCreator
import sql_scripts.ScriptsLoader
import utils.DBConfig
import utils.database.DBActions
import utils.database.DBActions.{DBActionResult, FragmentFunction}

import scala.concurrent.Future

trait TestRepositoryBase {

  def executeScript(script: String, configuration: DBConfig): Future[DBActionResult] = {
    implicit val transactor: Transactor.Aux[IO, Unit] = TransactionCreator.get(configuration)
    val fragmentFunction: FragmentFunction = ScriptsLoader.creators(configuration.dbName, script)
    DBActions.action(fragmentFunction)
  }
}
