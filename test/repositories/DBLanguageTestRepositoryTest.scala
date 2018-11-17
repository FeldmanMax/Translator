package repositories

import org.scalatest.FunSuite
import play.api.i18n.Lang
import utils.database.DBActions.DBActionResult
import utils.{DBConfig, FileConfigurationService}
import utils.database.DBResponseStatus
import utils.errors.{DBError, Failed}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future, duration}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class DBLanguageTestRepositoryTest extends FunSuite with TestRepositoryBase {

  test("test the whole flow") {
    val configuration: DBConfig = FileConfigurationService.get.dbConfig
    val flow: Future[DBActionResult] = for {
      _                         <-  executeScript("drop_translator_language.sql", configuration)
      createTranslatorDBResult  <-  executeScript("create_translator_language.sql", configuration)
      crudResult                <-  performCRUD
      dropTranslatorDBResult    <-  executeScript("drop_translator_language.sql", configuration)
    } yield {
      val actionResult: DBActionResult = createTranslatorDBResult.flatMap { createTableRes =>
        if(createTableRes.resCode != 0) {
          Left(Failed("Could not create a language table", DBError))
        }
        else {
          crudResult.flatMap { cresult =>
            if(cresult.resCode != 1)                  Left(Failed("CRUD failed", DBError))
            else {
              dropTranslatorDBResult.flatMap { dropResult =>
                if (dropResult.resCode != 0)          Left(Failed("Could not drop a language table", DBError))
                else                                  Right(DBResponseStatus(1))
              }
            }
          }
        }
      }
      actionResult
    }

    Await.ready(flow, new FiniteDuration(10000, duration.MILLISECONDS)).onComplete {
      case Success(value) =>  value match {
        case Left(error)    =>  assert(false, error.message)
        case Right(result)  =>  assert(result.resCode == 1, "Right(result)")
      }
      case Failure(e)     => assert(false, e)
    }
  }

  private def performCRUD: Future[DBActionResult] = {
    val lang: Lang = Lang("en-EN")
    val dbLanguageRepository: DBLanguageRepository = new DBLanguageRepository(FileConfigurationService)
    val flow: Future[DBActionResult] = for {
      emptyAllResult  <- dbLanguageRepository.getAll
      addResult       <- dbLanguageRepository.insert(lang)
      getAllResult    <- dbLanguageRepository.getAll
      deleteResult    <- dbLanguageRepository.delete(lang)
    } yield {
      val crudInnerResponse =
        for {
          finalEmptyAllResult <- emptyAllResult
          finalAddResult      <- addResult
          finalGetAllResult   <- getAllResult
          finalDeleteResult   <- deleteResult
        } yield {
          val retValue =
            if       (finalEmptyAllResult.nonEmpty)                 Left(Failed("language table was not empty", DBError))
            else if  (finalAddResult != lang)                       Left(Failed(s"$finalAddResult != $lang", DBError))
            else if  (!finalGetAllResult.headOption.contains(lang)) Left(Failed(s"${finalGetAllResult.headOption.contains(lang)} != $lang", DBError))
            else if  (finalDeleteResult != lang)                    Left(Failed(s"$finalDeleteResult != $lang", DBError))
            else                                                    Right(DBResponseStatus(1))
          retValue
        }
      val crudOuterResponse: DBActionResult = crudInnerResponse.flatMap { innerResult => innerResult.asInstanceOf[DBActionResult] }
      crudOuterResponse
    }
    flow
  }
}