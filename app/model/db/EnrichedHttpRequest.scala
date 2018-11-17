package model.db

import java.util.UUID

import org.joda.time.{DateTime, Period}
import play.api.mvc._

case class EnrichedHttpRequest(internalRequest: InternalHttpRequest, request: Request[AnyContent])

case class InternalHttpRequest(action: PerformedAction) {
  val startTime: DateTime = DateTime.now()
  lazy val endTime: DateTime = DateTime.now()
  val metadata: Metadata = Metadata(UUID.randomUUID())
  def timeTaken: Long = new Period(startTime, endTime).getMillis
}

case class Metadata(sessionId: UUID)

trait PerformedAction
case object AddLanguage     extends PerformedAction
case object DeleteLanguage  extends PerformedAction
case object GetAllLanguages extends PerformedAction


case class EnrichedHttpResponse(
  status:           Int,
  result:           Result,
  performedAction:  String,
  startTime:        String,
  endTime:          String,
  timeTaken:        String
)