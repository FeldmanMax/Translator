package repositories

import com.google.inject.{ImplementedBy, Inject, Singleton}
import play.api.i18n.Lang
import services.repositories.TransactionCreator
import doobie.util.fragment.Fragment
import utils.{ConfigurationService, DBConfig}
import doobie.util.transactor.Transactor
import doobie.implicits._
import cats.effect.IO
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import utils.Extensions._
import utils.GeneralTypes.ActualResult
import utils.database.DBActions
import utils.database.DBActions.DBActionResult
import utils.errors.{DBError, Failed, ValueExists}


@ImplementedBy(classOf[DBLanguageRepository])
trait LanguageRepository extends RepositoryBase {
  def getAll:                       Future[ActualResult[Vector[Lang]]]
  def delete(lang: Lang):           Future[ActualResult[Lang]]
  def insert(lang: Lang):              Future[ActualResult[Lang]]
  protected def exists(lang: Lang): Future[ActualResult[Boolean]]
}

@Singleton
final class DBLanguageRepository @Inject()(private val configurationService: ConfigurationService) extends LanguageRepository {
  private val dbConfig: DBConfig = configurationService.get.dbConfig
  private val transactor: Transactor.Aux[IO, Unit] = TransactionCreator.get(dbConfig)

  def getAll: Future[ActualResult[Vector[Lang]]] = {
    val queryFragment = FragmentCreator.create(s"select code from $activeTableName")
    val action = (codes: List[String]) => { Right(codes.map(code => Lang(code)).toVector)}
    queryFragment.query[String].stream.compile.toList.transact(transactor).unsafeToFuture() attemptAction action
  }

  def delete(lang: Lang): Future[ActualResult[Lang]] = {
    val queryFragment = FragmentCreator.create(s"delete from $activeTableName where code = '${lang.code}'")
    val deleteResult: Future[DBActionResult] = DBActions.action(() => Right(queryFragment))(transactor)
    val action: DBActionResult => ActualResult[Lang] = (actionResult: DBActionResult) => {
      actionResult.flatMap { result =>
             if(result.resCode == 0)  Left(Failed(s"Failed to delete $lang", DBError))
        else if(result.resCode == 1)  Right(lang)
        else                          Left(Failed(s"Unknown error. Result is: $result", DBError))
      }
    }
    deleteResult attemptAction action
  }

  def insert(lang: Lang): Future[ActualResult[Lang]] = {
    exists(lang).flatMap {
      case Left(error)                  => Left(error).toFuture
      case Right(ifExists) if ifExists  => Left(ValueExists(s"$lang exists", DBError)).toFuture
      case Right(ifExists) if !ifExists =>
        val sql: String = s"insert into $activeTableName (country, code) values ('${lang.country}', '${lang.code}')"
        val action: DBActionResult => ActualResult[Lang] = (actionResult: DBActionResult) => {
          actionResult.flatMap { dbResponseStatus =>
            dbResponseStatus.resCode == 1 cata (Failed(s"Failed to insert with response code: ${dbResponseStatus.resCode}", DBError), lang)
          }
        }
        DBActions.action(() => Right(FragmentCreator.create(sql)))(transactor) attemptAction (x=>action(x))
    }
  }

  protected def exists(lang: Lang): Future[ActualResult[Boolean]] = {
    val queryFragment: Fragment = FragmentCreator.create(s"select code from $activeTableName where code = '${lang.code}'")
    val future: Future[List[String]] = queryFragment.query[String].stream.compile.toList.transact(transactor).unsafeToFuture()
    val action: List[String] => ActualResult[Boolean] = (list: List[String]) => Right(list.size == 1)
    future attemptAction action
  }

  val activeTableName: String = s"${dbConfig.dbName}.public.language"
}
