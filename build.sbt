lazy val sbtGroll = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin, BuildInfoPlugin, GitVersioning)

organization := "de.heikoseeberger"
name         := "sbt-groll"
licenses     += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-target:jvm-1.6",
  "-encoding", "UTF-8"
)

unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value)
unmanagedSourceDirectories.in(Test)    := Seq(scalaSource.in(Test).value)

libraryDependencies ++= Seq(
  "com.typesafe"     %  "config"    % "1.3.1",
  "org.scalatest"    %% "scalatest" % "3.0.1" % Test
)
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.0")

git.useGitDescribe := true

sbtPlugin         := true
publishMavenStyle := false

import scalariform.formatter.preferences._
preferences := preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)

import de.heikoseeberger.sbtheader.license.Apache2_0
headers := Map("scala" -> Apache2_0("2015", "Heiko Seeberger"))

buildInfoPackage := s"${organization.value}.sbtgroll"
