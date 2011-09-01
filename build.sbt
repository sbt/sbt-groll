
organization := "name.heikoseeberger.groll"

name := "groll"

version := "0.2.0-SNAPSHOT"

scalaVersion := "2.9.1"

sbtPlugin := true

scalacOptions ++= Seq("-unchecked", "-deprecation")

publishTo := Some(Resolver.file("hseeberger", file("/Users/heiko/projects/hseeberger.github.com/snapshots"))(Resolver.ivyStylePatterns))
