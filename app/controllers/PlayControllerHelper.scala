package controllers

import model.db.{EnrichedHttpRequest, EnrichedHttpResponse, InternalHttpRequest, PerformedAction}
import play.api.mvc._
import utils.Formatters
import utils.GeneralTypes.ActualResult

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import utils.Extensions._

abstract class PlayControllerHelper(cc: ControllerComponents) extends AbstractController(cc) {

  def async(actionPerformed: PerformedAction,
            action: EnrichedHttpRequest => Future[Result]): Action[AnyContent] = {
    Action.async { req =>
      // Important: do NOT inline the creation of this instance. it is used for monitoring the request
      val internalHttpRequest: InternalHttpRequest = InternalHttpRequest(actionPerformed)
      val enrichedHttpRequest: EnrichedHttpRequest = EnrichedHttpRequest(internalHttpRequest, req)
      val result: Future[Result] = action(enrichedHttpRequest)

      result.flatMap { r =>
        val response: EnrichedHttpResponse = getEnrichedHttpResponse(r, internalHttpRequest)
        Future { println(response) }
      }

      result map { result => result }
    }
  }

  def resulting[T](actualResult: ActualResult[T], func: T => Result): Result = {
    actualResult to (error =>  error.status, result => func(result))
  }

  private def getEnrichedHttpResponse(result: Result, internalHttpRequest: InternalHttpRequest): EnrichedHttpResponse = {
    EnrichedHttpResponse(
      result.header.status,
      result,
      internalHttpRequest.action.toString,
      internalHttpRequest.startTime.toString(Formatters.DATE_TIME_PATTERN),
      internalHttpRequest.endTime.toString(Formatters.DATE_TIME_PATTERN),
      internalHttpRequest.timeTaken.toString
    )
  }
}