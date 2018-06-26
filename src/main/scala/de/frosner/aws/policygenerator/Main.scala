package de.frosner.aws.policygenerator

import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

object Main extends App {

  pureconfig.loadConfig[GeneratorConfig]("aws").map { config =>
    val generator = new Generator(config)
    val now = DateTime.now
    val policy = Policy(
      expiration = now + 1.day,
      conditions = Seq(
        ExactCondition("acl", "public-read"),
        StartsWithCondition("$Content-Type", "image/"),
        StartsWithCondition("$key", "upload/"),
        StartsWithCondition("$x-amz-meta-tag", "")
      )
    )
    generator.generate(now, policy)
  }

}
