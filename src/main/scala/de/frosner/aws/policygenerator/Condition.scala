package de.frosner.aws.policygenerator

import io.circe.Encoder
import io.circe.syntax._

sealed trait Condition

case class ExactCondition(key: String, value: String) extends Condition
case class StartsWithCondition(key: String, value: String) extends Condition

object Condition {
  implicit val encoder: Encoder[Condition] = Encoder.instance {
    case ExactCondition(key, value)      => Map((key, value)).asJson
    case StartsWithCondition(key, value) => Seq("starts-with", key, value).asJson
  }
}
