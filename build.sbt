// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `sbt-groll` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, GitVersioning)
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
      val config    = "1.3.1"
      val slf4jNop  = "1.7.25"
      val sbtGit    = "0.9.3"
      val scalaTest = "3.0.3"
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
  gitSettings ++
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
      "-encoding", "UTF-8"
    ),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value),
    shellPrompt in ThisBuild := { state =>
      val project = Project.extract(state).currentRef.project
      s"[$project]> "
    },
    sbtPlugin := true,
    publishMavenStyle := false
)

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtOnCompile.in(Sbt) := false,
    scalafmtVersion := "1.1.0"
  )
