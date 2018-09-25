// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `sbt-groll` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, SbtPlugin)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.config,
        library.scalaTest % Test,
        library.slf4jNop  % Test
      ),
      addSbtPlugin(library.sbtGit)
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val config    = "1.3.3"
      val slf4jNop  = "1.7.25"
      val sbtGit    = "1.0.0"
      val scalaTest = "3.0.5"
    }
    val config    = "com.typesafe"     %  "config"    % Version.config
    val sbtGit    = "com.typesafe.sbt" %  "sbt-git"   % Version.sbtGit
    val scalaTest = "org.scalatest"    %% "scalatest" % Version.scalaTest
    val slf4jNop  = "org.slf4j"        %  "slf4j-nop" % Version.slf4jNop
  }


// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  scalafmtSettings

lazy val commonSettings =
  Seq(
    // scalaVersion from .travis.yml via sbt-travisci
    // scalaVersion := "2.12.3",
    organization := "de.heikoseeberger",
    organizationName := "Heiko Seeberger",
    startYear := Some(2015),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-Ypartial-unification",
      "-Ywarn-unused-import"
    ),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value),
    publishMavenStyle := false
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true
  )
