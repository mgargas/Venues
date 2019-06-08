name := "venues"

version := "0.1"

scalaVersion := "2.13.0"

libraryDependencies ++= {
  val akkaVersion       = "2.5.11"
  val akkaHttpVersion   = "10.1.0"
  Seq(
    "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
  )
}