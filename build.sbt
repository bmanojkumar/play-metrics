name                := "play-metrics"
organization        := "com.phenom"
version             := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

libraryDependencies += "io.dropwizard.metrics" % "metrics-core" % "3.1.2"
libraryDependencies += "io.dropwizard.metrics" % "metrics-graphite" % "3.1.2"
