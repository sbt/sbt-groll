
organization := "name.heikoseeberger.sbt.groll"

name := "sbt-groll"

// version is defined in version.sbt in order to support sbt-release

sbtPlugin := true

scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq("org.scalaz" %% "scalaz-core" % "6.0.3")

publishTo <<= isSnapshot(if (_) Some(Classpaths.sbtPluginSnapshots) else Some(Classpaths.sbtPluginReleases))

publishMavenStyle := false

scalariformSettings

releaseSettings
