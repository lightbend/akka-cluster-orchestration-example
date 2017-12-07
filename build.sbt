name := "akka-cluster-tooling-example"

version := "0.1.0"

enablePlugins(SbtReactiveAppPlugin)

enableAkkaClusterBootstrap := Some(true)

enableServiceDiscovery := true

libraryDependencies ++= Vector(
  "com.typesafe.akka" %% "akka-actor"   % "2.5.7",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.7",
  "com.typesafe.akka" %% "akka-http"    % "10.0.11",
  "com.typesafe.akka" %% "akka-stream"  % "2.5.7")

endpoints += HttpEndpoint("http", 0, HttpIngress(Vector(80, 443), Vector.empty, Vector("/cluster-example")))
