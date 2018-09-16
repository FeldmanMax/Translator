package repositories

import model.db.TranslationKey
import org.scalatest.FunSuite
import utils.database.DBActions.DBActionResult
import utils.database.DBResponseStatus
import utils.errors.{DBError, Failed}
import utils.{DBConfig, FileConfigurationService}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future, duration}
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

class DBTranslatorKeyRepositoryTest extends FunSuite with TestRepositoryBase {
  test("test the whole flow") {
    val configuration: DBConfig = FileConfigurationService.get.dbConfig
    val flow: Future[DBActionResult] = for {
      _                         <-  executeScript("drop_translator_translations_key.sql", configuration)
      createTranslatorDBResult  <-  executeScript("create_translator_translations_key.sql", configuration)
      crudResult                <-  performCRUD
       dropTranslatorDBResult    <-  executeScript("drop_translator_translations_key.sql", configuration)
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

  private def performCRUD(): Future[DBActionResult] = {
    val tranKey: TranslationKey = TranslationKey(1, "key", "service", feature = "feature", isActive = true, null, null)
    val updatedTranKey: TranslationKey = tranKey.copy(key = "key_1", service = "service_1", isActive = false)
    val repository: TranslatorKeyRepository[Future] = new DBTranslatorKeyRepository(FileConfigurationService)
    for {
      emptyAllResult    <-  repository.getAll(service = "service", feature = "feature")
      insertResult      <-  repository.insert(key     = "key", service = "service", feature = "feature")
      getAllResult      <-  repository.getAll(service = "service", feature = "feature")
      byKeyResult       <-  repository.getByKey(key   = "key", service = "service", feature = "feature")
      updateResult      <-  repository.update(updatedTranKey)
    } yield {
      val crudInnerResponse = for {
        finalEmptyAllResult   <- emptyAllResult
        finalInsertResult     <- insertResult
        expectedInsertResult  = TranslationKey(id = 1, key = "key", service = "service", feature = "feature", isActive = true, null, null)
        finalGetAllResult     <- getAllResult
        finalByKeyResult      <- byKeyResult
      } yield {
        val retValue =
                if (finalEmptyAllResult.nonEmpty)                                         Left(Failed("TranslationKey table was not empty", DBError))
          else  if (!areSameTranslationKeys(finalInsertResult, expectedInsertResult))     Left(Failed(s"$finalInsertResult != $expectedInsertResult", DBError))
          else  if (finalGetAllResult.isEmpty ||
                    finalGetAllResult.size != 1 ||
                    !areSameTranslationKeys(finalGetAllResult.head, expectedInsertResult))  Left(Failed(s"finalGetAllResult failed", DBError))
          else  if (!areSameTranslationKeys(finalByKeyResult,   tranKey))             Left(Failed(s"Get by key failed", DBError))
          else  if (updateResult.isRight)                                             Left(Failed(s"Update failed. IsActive=true", DBError))
          else                                                                        Right(DBResponseStatus(1))

        retValue
      }
      val crudOuterResponse: DBActionResult = crudInnerResponse.flatMap { innerResult => innerResult.asInstanceOf[DBActionResult] }
      crudOuterResponse
    }
  }

  private def areSameTranslationKeys(result: TranslationKey, expected: TranslationKey): Boolean =
    result.id == expected.id && result.key == expected.key && result.service == expected.service && result.isActive == expected.isActive

}
