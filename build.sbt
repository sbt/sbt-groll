lazy val sbtGroll = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin, GitVersioning, BuildInfoPlugin)

name := "sbt-groll"

sbtPlugin := true

libraryDependencies ++= List(
  Library.config,
  Library.jGit,
  Library.scalaTest % "test"
)

initialCommands := """|import de.heikoseeberger.sbtgroll._""".stripMargin
