//package contollers
//
//import controllers.BackofficeController
//import org.scalatest.BeforeAndAfter
//import org.scalatest.mockito.MockitoSugar
//import org.scalatestplus.play.PlaySpec
//import play.api.i18n.Lang
//import play.api.mvc._
//import play.api.test.Helpers._
//import play.api.test._
//import repositories.LanguageRepository
//import repositories.statuses.{LangExists, LanguageStatus}
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//
//class BackofficeControllerTest
//  extends PlaySpec
//    with Results
//    with MockitoSugar
//    with ControllerMocks
//    with BeforeAndAfter {
//
//  before {
//    // create a database
//  }
//
//  "BackOffice controller with mocked injection" should {
//    "should return all the languages" in {
//      val langRepo = new LanguageRepository {
//        override def getAll: Future[List[Lang]] = {
//          Future {getListOfLanguages }
//        }
//        override def delete(lang: Lang): Future[Boolean] = Future { true }
//        override def add(lang: Lang): Future[LanguageStatus] = Future { LangExists }
//        override protected def exists(lang: Lang): Future[Boolean] = Future { true }
//      }
//      val controller: BackofficeController =
//        new BackofficeController(controllerComponents, actorSystem, readLang)
//      val langs = controller.getAll.apply(FakeRequest())
//      status(langs) must be(OK)
//      val result: String = contentAsString(langs)
//      assert(result.contains("de-DE"))
//      assert(result.contains("en-EN"))
//    }
//  }
//
//  private def getListOfLanguages: List[Lang] = List(Lang("en-EN"), Lang("de-DE"), Lang("he-IL"), Lang("ru-RU"))
//}
