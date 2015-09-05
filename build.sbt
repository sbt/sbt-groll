lazy val sbtGroll = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin, BuildInfoPlugin, GitVersioning)

organization    := "de.heikoseeberger"
name            := "sbt-groll"
git.baseVersion := "4.8.0"
licenses        += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-target:jvm-1.6",
  "-encoding", "UTF-8"
)

unmanagedSourceDirectories.in(Compile) := List(scalaSource.in(Compile).value)
unmanagedSourceDirectories.in(Test)    := List(scalaSource.in(Test).value)

testFrameworks := List(sbt.TestFrameworks.ScalaTest)

libraryDependencies ++= List(
  "com.typesafe"     % "config"           % "1.3.0",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.0.1.201506240215-r",
  "org.scalatest"    %% "scalatest"       % "2.2.5"                 % "test"
)

initialCommands := """|import de.heikoseeberger.sbtgroll._""".stripMargin

sbtPlugin         := true
publishMavenStyle := false

import scalariform.formatter.preferences._
preferences := preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)

import de.heikoseeberger.sbtheader.license.Apache2_0
HeaderPlugin.autoImport.headers := Map("scala" -> Apache2_0("2015", "Heiko Seeberger"))

buildInfoPackage := s"${organization.value}.sbtgroll"
