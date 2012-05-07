
organization := "name.heikoseeberger.groll"

name := "groll"

// version is defined in version.sbt in order to support sbt-release

sbtPlugin := true

scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq("org.scalaz" %% "scalaz-core" % "6.0.3")

// sbt 0.12:
//publishTo <<= isSnapshot(if (_) Some(Classpaths.sbtPluginSnapshots) else Some(Classpaths.sbtPluginReleases))
publishTo <<= isSnapshot { isSnapshot =>
  val SbtPluginRepositoryRoot = "http://scalasbt.artifactoryonline.com/scalasbt"
  def sbtPluginRepo(status: String) = Resolver.url("sbt-plugin-" + status, new URL(SbtPluginRepositoryRoot + "/sbt-plugin-" + status + "/"))(Resolver.ivyStylePatterns)
  if (isSnapshot) Some(sbtPluginRepo("snapshots")) else Some(sbtPluginRepo("releases"))
}

publishMavenStyle := false

sbtrelease.Release.releaseSettings

scalariformSettings

scriptedSettings
