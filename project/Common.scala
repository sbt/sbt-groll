import com.typesafe.sbt.SbtScalariform._
import sbt._
import sbt.Keys._
import sbtbuildinfo.Plugin._
import sbtrelease.ReleasePlugin._
import scalariform.formatter.preferences._

object Common {

  val settings =
    scalariformSettings ++
    releaseSettings ++
    buildInfoSettings ++
    List(
      // Core settings
      organization := "name.heikoseeberger",
      scalaVersion := Version.scala,
      crossScalaVersions := List(scalaVersion.value),
      scalacOptions ++= List(
        "-unchecked",
        "-deprecation",
        "-language:_",
        "-target:jvm-1.6",
        "-encoding", "UTF-8"
      ),
      javacOptions ++= List(
        "-source", "1.6",
        "-target", "1.6"
      ),
      unmanagedSourceDirectories in Compile := List((scalaSource in Compile).value),
      unmanagedSourceDirectories in Test := List((scalaSource in Test).value),
      sbtPlugin := true,
      // Publish settings
      publishTo := Some(if (isSnapshot.value) Classpaths.sbtPluginSnapshots else Classpaths.sbtPluginReleases),
      publishMavenStyle := false,
      // Scalariform settings
      ScalariformKeys.preferences := ScalariformKeys.preferences.value
        .setPreference(AlignArguments, true)
        .setPreference(AlignParameters, true)
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
        .setPreference(DoubleIndentClassDeclaration, true),
      // Release settings
      ReleaseKeys.versionBump := sbtrelease.Version.Bump.Minor,
      // BuildInfo settings
      sourceGenerators in Compile <+= buildInfo,
      buildInfoPackage := "name.heikoseeberger.sbtgroll"
    )
}
