name := "akka-cluster-orchestration-example"

version := "0.1.0"

enablePlugins(SbtReactiveAppPlugin)

enableAkkaClusterBootstrap := true

libraryDependencies ++= Vector(
  "com.typesafe.akka" %% "akka-actor"   % "2.5.11",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.11",
  "com.typesafe.akka" %% "akka-http"    % "10.0.11",
  "com.typesafe.akka" %% "akka-stream"  % "2.5.7")

endpoints += HttpEndpoint("http", HttpIngress(Vector(80, 443), Vector.empty, Vector("/cluster-example")))

mainClass in Compile := Some("com.lightbend.example.com.lightbend.rp.example.akkacluster.App")

applications += "my-job" -> Vector("bin/job")

deployMinikubeRpArguments ++= Vector(
  "--ingress-annotation", "ingress.kubernetes.io/rewrite-target=/",
  "--ingress-annotation", "nginx.ingress.kubernetes.io/rewrite-target=/"
)

deployMinikubeAkkaClusterBootstrapContactPoints := 3
