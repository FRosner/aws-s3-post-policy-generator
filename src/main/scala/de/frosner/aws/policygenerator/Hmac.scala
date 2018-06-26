package de.frosner.aws.policygenerator

import java.nio.charset.{Charset, StandardCharsets}

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Hmac {
  def hash(secret: Array[Byte], toHash: String): Array[Byte] = {
    val method = "HmacSHA256"
    val key = new SecretKeySpec(secret, method)
    val mac = Mac.getInstance(method)
    mac.init(key)
    mac.doFinal(toHash.getBytes(StandardCharsets.UTF_8))
  }
}
