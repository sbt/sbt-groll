// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `sbt-groll` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, BuildInfoPlugin, GitVersioning)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.config,
        library.scalaTest % Test
      ),
      addSbtPlugin(library.sbtGit)
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val config     = "1.3.1"
      val sbtGit     = "0.9.0"
      val scalaTest  = "3.0.1"
    }
    val config     = "com.typesafe"     %  "config"    % Version.config
    val sbtGit     = "com.typesafe.sbt" % "sbt-git"    % Version.sbtGit
    val scalaTest  = "org.scalatest"    %% "scalatest" % Version.scalaTest
  }


// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  gitSettings ++
  headerSettings ++
  pluginSettings ++
  buildInfoSettings

lazy val commonSettings =
  Seq(
    // scalaVersion from .travis.yml via sbt-travisci
    // scalaVersion := "2.12.1
    organization := "de.heikoseeberger",
    licenses += ("Apache 2.0",
                 url("http://www.apache.org/licenses/LICENSE-2.0")),
    mappings.in(Compile, packageBin) += baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.6",
      "-encoding", "UTF-8"
    ),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value),
    shellPrompt in ThisBuild := { state =>
      val project = Project.extract(state).currentRef.project
      s"[$project]> "
    }
)

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

import de.heikoseeberger.sbtheader.license._
lazy val headerSettings =
  Seq(
    headers := Map("scala" -> Apache2_0("2015", "Heiko Seeberger"))
  )

lazy val pluginSettings =
  Seq(
    sbtPlugin := true,
    publishMavenStyle := false
  )

lazy val buildInfoSettings =
  Seq(
    buildInfoPackage := s"${organization.value}.sbtgroll"
  )
