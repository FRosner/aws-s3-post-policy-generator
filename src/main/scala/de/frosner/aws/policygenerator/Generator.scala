package de.frosner.aws.policygenerator

import java.nio.charset.StandardCharsets
import java.util.Base64

import org.joda.time.DateTime
import io.circe.syntax._
import io.circe.generic.auto.exportEncoder
import DateTimeJson.encoder
import javax.xml.bind.DatatypeConverter

class Generator(generatorConfig: GeneratorConfig) {

  def generate(date: DateTime, policy: Policy) = {
    val allConditions = policy.conditions ++ Seq(
      ExactCondition("bucket", generatorConfig.bucket),
      ExactCondition("x-amz-algorithm", "AWS4-HMAC-SHA256"),
      ExactCondition(
        "x-amz-credential",
        generatorConfig.accessKey + "/" + toAwsShortDateFormat(date) + "/" + generatorConfig.region + "/s3/aws4_request"
      ),
      ExactCondition("x-amz-date", toAwsIso8601DateFormat(date))
    )

    val completePolicy = policy.copy(conditions = allConditions)
    val policyJson = completePolicy.asJson
    println(policyJson)
    val base64Policy = Base64.getEncoder.encodeToString(policyJson.toString.getBytes(StandardCharsets.UTF_8))
    println(base64Policy)
    val signature = sign(generatorConfig.secretKey, date, generatorConfig.region, base64Policy, "s3")
    println(signature)

    println(
      sign(
        secretKey = "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY",
        date = DateTime.parse("2012-02-15T00:00"),
        region = "us-east-1",
        base64Policy = "bla",
        serviceName = "iam"
      )
    )

    println(
      sign(
        secretKey = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
        date = DateTime.parse("2015-12-29T00:00"),
        region = "us-east-1",
        base64Policy =
          "eyAiZXhwaXJhdGlvbiI6ICIyMDE1LTEyLTMwVDEyOjAwOjAwLjAwMFoiLA0KICAiY29uZGl0aW9ucyI6IFsNCiAgICB7ImJ1Y2tldCI6ICJzaWd2NGV4YW1wbGVidWNrZXQifSwNCiAgICBbInN0YXJ0cy13aXRoIiwgIiRrZXkiLCAidXNlci91c2VyMS8iXSwNCiAgICB7ImFjbCI6ICJwdWJsaWMtcmVhZCJ9LA0KICAgIHsic3VjY2Vzc19hY3Rpb25fcmVkaXJlY3QiOiAiaHR0cDovL3NpZ3Y0ZXhhbXBsZWJ1Y2tldC5zMy5hbWF6b25hd3MuY29tL3N1Y2Nlc3NmdWxfdXBsb2FkLmh0bWwifSwNCiAgICBbInN0YXJ0cy13aXRoIiwgIiRDb250ZW50LVR5cGUiLCAiaW1hZ2UvIl0sDQogICAgeyJ4LWFtei1tZXRhLXV1aWQiOiAiMTQzNjUxMjM2NTEyNzQifSwNCiAgICB7IngtYW16LXNlcnZlci1zaWRlLWVuY3J5cHRpb24iOiAiQUVTMjU2In0sDQogICAgWyJzdGFydHMtd2l0aCIsICIkeC1hbXotbWV0YS10YWciLCAiIl0sDQoNCiAgICB7IngtYW16LWNyZWRlbnRpYWwiOiAiQUtJQUlPU0ZPRE5ON0VYQU1QTEUvMjAxNTEyMjkvdXMtZWFzdC0xL3MzL2F3czRfcmVxdWVzdCJ9LA0KICAgIHsieC1hbXotYWxnb3JpdGhtIjogIkFXUzQtSE1BQy1TSEEyNTYifSwNCiAgICB7IngtYW16LWRhdGUiOiAiMjAxNTEyMjlUMDAwMDAwWiIgfQ0KICBdDQp9",
        serviceName = "s3"
      )
    )

  }

  private def toAwsShortDateFormat(dateTime: DateTime): String =
    dateTime.toString("yyyyMMdd")

  private def toAwsIso8601DateFormat(dateTime: DateTime): String =
    dateTime.toString("yyyyMMdd'T'HHmmss'Z'")

  private def sign(
    secretKey: String,
    date: DateTime,
    region: String,
    base64Policy: String,
    serviceName: String
  ): String = {
    val dateKey = Hmac.hash(("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8), toAwsShortDateFormat(date))
    val regionKey = Hmac.hash(dateKey, region)
    val serviceKey = Hmac.hash(regionKey, serviceName)
    val signingKey = Hmac.hash(serviceKey, "aws4_request")
    val signature = DatatypeConverter.printHexBinary(Hmac.hash(signingKey, base64Policy)).toLowerCase
    println(s"policy   = $base64Policy")
    println(
      s"kSecret  = ${DatatypeConverter.printHexBinary(("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8)).toLowerCase}"
    )
    println(s"kDate    = ${DatatypeConverter.printHexBinary(dateKey).toLowerCase}")
    println(s"kRegion  = ${DatatypeConverter.printHexBinary(regionKey).toLowerCase}")
    println(s"kService = ${DatatypeConverter.printHexBinary(serviceKey).toLowerCase}")
    println(s"kSigning = ${DatatypeConverter.printHexBinary(signingKey).toLowerCase}")
    signature
  }

}
