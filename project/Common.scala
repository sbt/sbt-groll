import com.typesafe.sbt.SbtScalariform._
import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin._
import scalariform.formatter.preferences._

object Common {

  val settings =
    scalariformSettings ++
    releaseSettings ++
    List(
      // Core settings
      organization := "name.heikoseeberger",
      version := "0.1.0",
      scalaVersion := Version.scala,
      crossScalaVersions := List(scalaVersion.value),
      scalacOptions ++= List(
        "-unchecked",
        "-deprecation",
        "-language:_",
        "-target:jvm-1.7",
        "-encoding", "UTF-8"
      ),
      unmanagedSourceDirectories in Compile := List((scalaSource in Compile).value),
      unmanagedSourceDirectories in Test := List((scalaSource in Test).value),
      sbtPlugin := true,
      // Publish settings
      publishTo := Some(if (isSnapshot.value) Classpaths.sbtPluginSnapshots else Classpaths.sbtPluginReleases),
      publishMavenStyle := false,
      // Scalariform settings
      ScalariformKeys.preferences := ScalariformKeys.preferences.value
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
        .setPreference(DoubleIndentClassDeclaration, true)
        .setPreference(PreserveDanglingCloseParenthesis, true),
      // Release settings
      ReleaseKeys.versionBump := sbtrelease.Version.Bump.Minor
    )
}
