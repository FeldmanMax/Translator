package repositories

import com.google.inject.{ImplementedBy, Inject, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.sql.SQLException
import java.time.Instant

import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.implicits._
import services.repositories.TransactionCreator
import model.db.TranslationKey
import utils.{ConfigurationService, DBConfig}
import utils.GeneralTypes.ActualResult
import utils.Extensions._
import utils.database.DBActions.DBActionResult
import utils.database.DBActions
import utils.errors.{DBError, Failed, SqlError}

@ImplementedBy(classOf[DBTranslatorKeyRepository])
trait TranslatorKeyRepository[F[_]] extends RepositoryBase {
  def getByKey(key: String, service: String, feature: String):  F[ActualResult[TranslationKey]]
  def getAll(service: String, feature: String):                 F[ActualResult[List[TranslationKey]]]
  def insert(key: String, service: String, feature: String):    F[ActualResult[TranslationKey]]
  def update(key: TranslationKey):                              F[ActualResult[TranslationKey]]
}

@Singleton
final class DBTranslatorKeyRepository @Inject()(
  private val configurationService: ConfigurationService
) extends TranslatorKeyRepository[Future] {

  lazy val activeTableName: String = s"${dbConfig.dbName}.public.translations_key"
  private val dbConfig: DBConfig = configurationService.get.dbConfig
  private val transactor: Transactor.Aux[IO, Unit] = TransactionCreator.get(dbConfig)

  def getByKey(key: String, service: String, feature: String): Future[ActualResult[TranslationKey]] = {
    val queryFragment =
      s"select * from $activeTableName where key='$key' and service='$service' and feature='$feature' and is_active = 'true'".toFragment
    val future: Future[Either[SQLException, TranslationKey]] = queryFragment.query[TranslationKey].unique.transact(transactor).attemptSql.unsafeToFuture()
    val action = (e: Either[SQLException, TranslationKey]) => e.to (sqlError => Left(SqlError(sqlError)), tranKey => Right(tranKey))
    future attemptAction action
  }

  def getAll(service: String, feature: String): Future[ActualResult[List[TranslationKey]]] = {
    val queryFragment = s"select * from $activeTableName where service='$service' and feature='$feature' and is_active = 'true'".toFragment
    val future: Future[Either[SQLException, List[TranslationKey]]] = queryFragment.query[TranslationKey].stream.compile.toList.transact(transactor).attemptSql.unsafeToFuture()
    val action = (e: Either[SQLException, List[TranslationKey]]) => e.to (sqlError => Left(SqlError(sqlError)), tranKey => Right(tranKey))
    future attemptAction action
  }

  def insert(key: String, service: String, feature: String): Future[ActualResult[TranslationKey]] = {
    val queryFragment = s"insert into $activeTableName (key, service, feature) values ('$key', '$service', '$feature')".toFragment
    val action: DBActionResult => ActualResult[Unit] = (actionResult: DBActionResult) =>
      updateAction(actionResult)((code: Int) => Failed(s"Failed to insert with response code: $code", DBError))
    (DBActions.action(() => Right(queryFragment))(transactor) attemptAction action).flatMap { _ => getByKey(key, service, feature) }
  }

  def update(key: TranslationKey): Future[ActualResult[TranslationKey]] = {
    val queryFragment = (s"update  $activeTableName " +
                         s" set   is_active          = '${key.isActive}'" +
                         s" ,     key                = '${key.key}'" +
                         s" ,     service            = '${key.service}'" +
                         s" ,     feature            = '${key.feature}'" +
                         s" ,     update_timestamp   = '${Instant.now().toString}'" +
                         s" where id                 =  ${key.id}").toFragment
    val action: DBActionResult  =>  ActualResult[Unit] = (actionResult: DBActionResult) => {
      updateAction(actionResult)((code: Int) => Failed(s"Failed to update with response code: $code", DBError))
    }
    (DBActions.action(() => Right(queryFragment))(transactor) attemptAction action).flatMap { _ => getByKey(key.key, key.service, key.feature) }
  }

  private def updateAction(actionResult: DBActionResult)(error: Int => utils.errors.Error): ActualResult[Unit] =
    actionResult.flatMap { dbResponseStatus => dbResponseStatus.resCode == 1 cata (error(dbResponseStatus.resCode), ()) }
}