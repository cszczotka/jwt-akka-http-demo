name := "jwt-akka-http-demo"

version := "1.0"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.20",
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "com.typesafe.akka" %% "akka-http-core" % "10.1.7",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7",
  "io.spray" %%  "spray-json" % "1.3.4",
  "io.igl" %% "jwt" % "1.2.2",
  "com.h2database" % "h2" % "1.4.196",
  "org.json4s"       %% "json4s-jackson"     % "3.5.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.7" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.20" % "test",
  "org.scalatest" %% "scalatest" % "3.0.2" % "test"
)

