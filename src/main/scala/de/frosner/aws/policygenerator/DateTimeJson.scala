package de.frosner.aws.policygenerator

import io.circe.{Encoder, Json}
import org.joda.time.DateTime

object DateTimeJson {
  implicit val encoder: Encoder[DateTime] = new Encoder[DateTime] {
    final def apply(a: DateTime): Json = Json.fromString(a.toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
  }
}
