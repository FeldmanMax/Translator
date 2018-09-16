package serialization

import io.circe.{Encoder, Json}
import model.db.EnrichedHttpResponse

object HttpResponseSerialization {
  implicit val enrichedHttpResponseEncoder: Encoder[EnrichedHttpResponse] = new Encoder[EnrichedHttpResponse] {
    final def apply(a: EnrichedHttpResponse): Json = {
      Json.obj("status"        -> Json.fromInt(a.status),
                      "result"    -> Json.fromString(a.result.toString()),
                      "action"     -> Json.fromString(a.performedAction),
                      "startTime"     -> Json.fromString(a.startTime),
                      "endTime"     -> Json.fromString(a.endTime),
                      "taken"     -> Json.fromString(a.timeTaken + " ms")
      )
    }
  }
}
