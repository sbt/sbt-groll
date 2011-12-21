
import sbtrelease._

organization := "name.heikoseeberger.groll"

name := "groll"

// version is defined in version.sbt in order to support sbt-release

sbtPlugin := true

scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq("org.scalaz" %% "scalaz-core" % "6.0.3")

publishTo <<= (version) { version =>
  def hseeberger(name: String) =
    Resolver.file("hseeberger-%s" format name, file("/Users/heiko/projects/hseeberger.github.com/%s" format name))(Resolver.ivyStylePatterns)
  val resolver =
    if (version endsWith "SNAPSHOT") hseeberger("snapshots")
    else hseeberger("releases")
  Option(resolver)
}

publishMavenStyle := false

seq(posterousSettings: _*)

(email in Posterous) <<= PropertiesKeys.properties(_ get "posterous.email")

(password in Posterous) <<= PropertiesKeys.properties(_ get "posterous.password")

seq(propertiesSettings: _*)

seq(Release.releaseSettings: _*)

ReleaseKeys.releaseProcess <<= thisProjectRef { ref =>
  import ReleaseStateTransformations._
  Seq[ReleasePart](
    initialGitChecks,
    checkSnapshotDependencies,
    releaseTask(check in Posterous in ref),
    inquireVersions,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseTask(publish in Global in ref),
    releaseTask(publish in Posterous in ref),
    setNextVersion,
    commitNextVersion
  )
}

seq(scalariformSettings: _*)

seq(scriptedSettings: _*)
