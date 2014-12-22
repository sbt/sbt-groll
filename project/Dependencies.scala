import sbt._

object Version {
  val config    = "1.2.1"
  val jGit      = "3.5.3.201412180710-r"
  val scala     = "2.10.4"
  val scalaTest = "2.2.2"
}

object Library {
  val config    = "com.typesafe"     % "config"           % Version.config
  val jGit      = "org.eclipse.jgit" % "org.eclipse.jgit" % Version.jGit
  val scalaTest = "org.scalatest"    %% "scalatest"       % Version.scalaTest
}
