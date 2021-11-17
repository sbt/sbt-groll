// *****************************************************************************
// Build settings
// *****************************************************************************

inThisBuild(
  Seq(
    organization     := "de.heikoseeberger",
    organizationName := "Heiko Seeberger",
    startYear        := Some(2016),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("https://github.com/sbt/sbt-groll")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/sbt/sbt-groll"),
        "git@github.com:sbt/sbt-groll.git"
      )
    ),
    developers := List(
      Developer(
        "hseeberger",
        "Heiko Seeberger",
        "mail@heikoseeberger.rocks",
        url("https://github.com/hseeberger")
      )
    ),
    // scalaVersion defined by sbt
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-encoding",
      "UTF-8",
      "-Ywarn-unused:imports",
    ),
    scalafmtOnCompile := true,
    dynverSeparator   := "_", // the default `+` is not compatible with docker tags
  )
)

// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `sbt-groll` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, SbtPlugin)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        library.config,
        library.scalaTest % Test,
        library.slf4jNop  % Test
      ),
      addSbtPlugin(library.sbtGit),
    )

// *****************************************************************************
// Project settings
// *****************************************************************************

lazy val commonSettings =
  Seq(
    // Also (automatically) format build definition together with sources
    Compile / scalafmt := {
      val _ = (Compile / scalafmtSbt).value
      (Compile / scalafmt).value
    },
  )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val config    = "1.4.1"
      val sbtGit    = "1.0.2"
      val slf4jNop  = "1.7.32"
      val scalaTest = "3.2.10"
    }
    val config    = "com.typesafe"     % "config"    % Version.config
    val sbtGit    = "com.typesafe.sbt" % "sbt-git"   % Version.sbtGit
    val scalaTest = "org.scalatest"   %% "scalatest" % Version.scalaTest
    val slf4jNop  = "org.slf4j"        % "slf4j-nop" % Version.slf4jNop
  }
