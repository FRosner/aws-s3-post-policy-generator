package de.frosner.aws.policygenerator

import org.joda.time.DateTime

case class Policy(expiration: DateTime, conditions: Seq[Condition])
