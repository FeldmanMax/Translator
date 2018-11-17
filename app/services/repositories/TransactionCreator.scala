package services.repositories

import cats.effect.IO
import doobie.util.transactor.Transactor
import utils.DBConfig

object TransactionCreator {
  def get(dbConfig: DBConfig): Transactor.Aux[IO, Unit] = {
    val transactor: Transactor.Aux[IO, Unit] = Transactor.fromDriverManager[IO](
      dbConfig.driver,
      dbConfig.fullUrl,
      dbConfig.user,
      dbConfig.password
    )

    transactor
  }
}
