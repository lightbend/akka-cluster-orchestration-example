name := "akka-cluster-orchestration-example"

version := "0.1.0"

libraryDependencies ++= Vector(
  "com.typesafe.akka" %% "akka-actor"   % "2.5.11",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.11",
  "com.typesafe.akka" %% "akka-http"    % "10.0.11",
  "com.typesafe.akka" %% "akka-stream"  % "2.5.7")
mainClass in Compile := Some("com.lightbend.example.com.lightbend.rp.example.akkacluster.App")
