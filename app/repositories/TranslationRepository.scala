//package repositories
//
//import cats.effect.IO
//import com.google.inject.{ImplementedBy, Inject, Singleton}
//import doobie.util.fragment.Fragment
//import doobie.util.transactor.Transactor
//import doobie.implicits._
//import model.db.Translation
//import play.api.i18n.Lang
//import services.repositories.TransactionCreator
//import utils.{ConfigurationService, DBConfig}
//import utils.GeneralTypes.ActualResult
//import utils.Extensions._
//
//import scala.concurrent.Future
//
//@ImplementedBy(classOf[DBTranslationRepository])
//trait TranslationRepository extends RepositoryBase {
//  def getAll:                                     Future[ActualResult[Vector[Translation]]]
//  def delete(key: String, lang: Lang):            Future[ActualResult[Lang]]
//  def add(translation: Translation):              Future[ActualResult[Translation]]
//  def update(translation: Translation):           Future[ActualResult[Translation]]
//  protected def exists(translation: Translation): Future[ActualResult[Boolean]]
//}
//
//@Singleton
//final class DBTranslationRepository @Inject()(private val configurationService: ConfigurationService) extends TranslationRepository {
//  private val dbConfig: DBConfig = configurationService.get.dbConfig
//  private val transactor: Transactor.Aux[IO, Unit] = TransactionCreator.get(dbConfig)
//
//  def getAll: Future[ActualResult[Vector[Translation]]] = {
////    val queryFragment: Fragment = FragmentCreator.create(s"select key, l.code, translation " +
////      s"from $activeTableName t inner join language l" +
////      s"on t.lang_id = l.id")
////    val action = (list: List[(String, String, String)]) => {
////      val result = list.map { case (key, code, translation) => Translation(key, lang = Lang(code), translation) }.toVector
////      Right(result)
////    }
////    queryFragment.query[(String, String, String)].stream.compile.toList.transact(transactor).unsafeToFuture() attempt (x=>action(x))
//  }
//
//  def delete(key: String, lang: Lang): Future[ActualResult[Lang]] = {
//    val queryFragment: Fragment = FragmentCreator.create(s"delete from $activeTableName where key = '$key' and lang='${lang.code}'")
//  }
//
//  override def add(translation: Translation): Future[ActualResult[Translation]] = ???
//
//  override def update(translation: Translation): Future[ActualResult[Translation]] = ???
//
//  override protected def exists(translation: Translation): Future[ActualResult[Boolean]] = ???
//
//  protected val activeTableName: String = s"${dbConfig.dbName}.public.translations"
//}