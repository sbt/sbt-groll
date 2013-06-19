
organization := "name.heikoseeberger"

name := "sbt-groll"

// TODO Move version to version.sbt in order to support sbt-release
version := "1.6.0"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-language:_",
  "-target:jvm-1.6",
  "-encoding", "UTF-8"
)

sbtPlugin := true

publishTo := { 
  import Classpaths._
  val repo = if (isSnapshot.value) sbtPluginSnapshots else sbtPluginReleases
  Some(repo)
}

publishMavenStyle := false
