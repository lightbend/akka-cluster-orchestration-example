# Akka Cluster Tooling Example

This is a very simple [Akka Cluster](https://doc.akka.io/docs/akka/snapshot/cluster-usage.html) and 
[Akka HTTP](https://doc.akka.io/docs/akka-http/current/scala/http/) application. Its main purpose is to show how you 
can build a similar application and take advantage of the [Platform Tooling](https://s3-us-west-2.amazonaws.com/rp-tooling-temp-docs/home.html)
to easily and safely deploy to [Kubernetes](https://kubernetes.io/) with minimal configuration required.

### Project Setup

> These steps have already been performed but they're included below for reference.

1. Add the following to `project/plugins.sbt`:

```scala
addSbtPlugin("com.lightbend.rp" % "sbt-reactive-app" % "0.4.0")
```

2. Enable the modules and declare the HTTP endpoint in `build.sbt`:

```scala
enablePlugins(SbtReactiveAppPlugin)

enableAkkaClusterBootstrap := Some(true)

enableServiceDiscovery := true

endpoints += HttpEndpoint("http", 0, HttpIngress(Vector(80, 443), Vector.empty, Vector("/cluster-example")))
```

3. Use the http endpoint declared above, see below from `Main.scala`:

```scala
val host = SocketBinding.bindHost("http", default = "localhost")
val port = SocketBinding.bindPort("http", default = 8080)

Http().bindAndHandle(route, host, port)
```

### Deployment

Prerequisites:

* [reactive-cli](https://s3-us-west-2.amazonaws.com/rp-tooling-temp-docs/deployment-setup.html#install-the-cli)
* [Minikube](https://github.com/kubernetes/minikube#installation)

> **You'll need version 0.4.2 (or later) of the CLI.**

1) Delete minikube:

If you want to start with a fresh Minikube, use the following. This will delete your existing Minikube and all of its data.

```bash
minikube delete

# optionally, remove everything
rm -rf ~/.minikube
```

2) Start minikube

```bash
minikube start

eval $(minikube docker-env)

minikube addons enable ingress
```

3) Build project

```bash
sbt docker:publishLocal
```

4) Deploy project

```bash
rp generate-kubernetes-deployment akka-cluster-tooling-example/akka-cluster-tooling-example:0.1.0 \
  --ingress-annotation ingress.kubernetes.io/rewrite-target=/ \
  --pod-controller-replicas 3 | kubectl apply -f -
```

5) View Results (Note: You'll get an SSL warning because the certificate is self-signed. This is okay.)

```bash
kubectl get all

echo "https://$(minikube ip)/cluster-example"
```
