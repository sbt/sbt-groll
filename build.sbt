lazy val sbtGroll = project in file(".")

name := "sbt-groll"

libraryDependencies ++= List(
  Library.config,
  Library.jGit,
  Library.scalaTest % "test"
)

initialCommands := """|import de.heikoseeberger.sbtgroll._""".stripMargin
