name := "akka-cluster-orchestration-example"

version := "0.1.0"

enablePlugins(SbtReactiveAppPlugin, Cinnamon)

libraryDependencies ++= Vector(
  "com.typesafe.akka" %% "akka-actor"   % "2.5.14",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.14",
  "com.typesafe.akka" %% "akka-http"    % "10.1.3",
  "com.typesafe.akka" %% "akka-stream"  % "2.5.14"
)

libraryDependencies ++= Vector(
  Cinnamon.library.cinnamonCHMetrics,
  Cinnamon.library.cinnamonAkka,
  Cinnamon.library.cinnamonAkkaHttp,
  Cinnamon.library.cinnamonJvmMetricsProducer,
  Cinnamon.library.cinnamonPrometheus,
  Cinnamon.library.cinnamonPrometheusHttpServer
)

mainClass in Compile := Some("com.lightbend.example.com.lightbend.rp.example.akkacluster.App")

enableAkkaClusterBootstrap := true

endpoints += TcpEndpoint("cinnamon", 9091, None)
endpoints += HttpEndpoint("http", 8080, HttpIngress(Seq(80), Seq("tutorial.io"), Seq("/")))

annotations := Map(
  // enable scraping
  "prometheus.io/scrape" -> "true",
  "prometheus.io/port" -> "9091"
)
