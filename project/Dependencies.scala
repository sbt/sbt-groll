import sbt._

object Version {
  val akka      = "2.3.3"
  val logback   = "1.1.2"
  val scala     = "2.11.1"
  val scalaTest = "2.1.7"
}

object Library {
  val akkaActor      = "com.typesafe.akka" %% "akka-actor"      % Version.akka
  val akkaSlf4j      = "com.typesafe.akka" %% "akka-slf4j"      % Version.akka
  val akkaTestkit    = "com.typesafe.akka" %% "akka-testkit"    % Version.akka
  val logbackClassic = "ch.qos.logback"    %  "logback-classic" % Version.logback
  val scalaTest      = "org.scalatest"     %% "scalatest"       % Version.scalaTest
}

object Dependencies {

  import Library._

  val sbtGroll = List(
    scalaTest % "test"
  )
}
