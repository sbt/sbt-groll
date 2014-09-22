lazy val sbtGroll = project in file(".")

name := "sbt-groll"

Common.settings

libraryDependencies ++= Dependencies.sbtGroll

initialCommands := """|import de.heikoseeberger.sbtgroll._""".stripMargin
