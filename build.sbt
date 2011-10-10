
organization := "name.heikoseeberger.groll"

name := "groll"

version := "0.5.0-SNAPSHOT"

sbtPlugin := true

scalacOptions ++= Seq("-unchecked", "-deprecation")

publishTo <<= (version) { version =>
  def hseeberger(name: String) =
    Resolver.file("hseeberger-%s" format name, file("/Users/heiko/projects/hseeberger.github.com/%s" format name))(Resolver.ivyStylePatterns)
  val resolver =
    if (version endsWith "SNAPSHOT") hseeberger("snapshots")
    else hseeberger("releases")
  Option(resolver)
}

publishMavenStyle := false
