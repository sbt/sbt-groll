import sbt._

object Version {
  val scala     = "2.10.4"
  val jgit      = "3.1.0.201310021548-r"
  val scalaTest = "2.1.2"
}

object Library {
  val jgit      = "org.eclipse.jgit" % "org.eclipse.jgit" % Version.jgit
  val scalaTest = "org.scalatest"    %% "scalatest"       % Version.scalaTest
}

object Dependencies {

  import Library._

  val sbtGroll = List(
    jgit,
    scalaTest % "test"
  )
}
