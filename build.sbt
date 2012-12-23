
organization := "name.heikoseeberger"

name := "sbt-groll"

// version is defined in version.sbt in order to support sbt-release

sbtPlugin := true

scalacOptions ++= Seq("-unchecked", "-deprecation")

publishTo <<= isSnapshot(if (_) Some(Classpaths.sbtPluginSnapshots) else Some(Classpaths.sbtPluginReleases))

publishMavenStyle := false

scalariformSettings

releaseSettings
