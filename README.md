# Akka Cluster Tooling Example

This is a very simple [Akka Cluster](https://doc.akka.io/docs/akka/snapshot/cluster-usage.html) and 
[Akka HTTP](https://doc.akka.io/docs/akka-http/current/scala/http/) application. Its main purpose is to show how you 
can build a similar application and take advantage of the [Platform Tooling](https://s3-us-west-2.amazonaws.com/rp-tooling-temp-docs/home.html)
to easily and safely deploy to [Kubernetes](https://kubernetes.io/) with minimal configuration required.

### Project Setup

> These steps have already been performed but they're included below for reference.

1. Add the following to `project/plugins.sbt`:

```scala
addSbtPlugin("com.lightbend.rp" % "sbt-reactive-app" % "0.3.2")
```

2. Enable the modules and declare the HTTP endpoint in `build.sbt`:

```scala
enablePlugins(SbtReactiveAppPlugin)

enableAkkaClusterBootstrap := Some(true)

enableServiceDiscovery := true

endpoints := endpoints.value :+ HttpEndpoint("http", 0, HttpIngress(Vector(80, 443), Vector.empty, Vector("/cluster-example")))
```

### Deployment

Prerequisites:

* [reactive-cli](https://s3-us-west-2.amazonaws.com/rp-tooling-temp-docs/deployment-setup.html#install-the-cli)
* [Minikube](https://github.com/kubernetes/minikube#installation)

1) Start minikube:

```bash
minikube start

eval $(minikube docker-env)
```

2) Build project

```bash
sbt docker:publishLocal
```

3) Deploy project

```bash
rp generate-kubernetes-deployment akka-cluster-tooling-example/akka-cluster-tooling-example:0.1.0 \
  --ingress-annotation ingress.kubernetes.io/rewrite-target=/ \
  --pod-controller-replicas 3 | kubectl apply -f -
```

4) View Results (Note: You'll get an SSL warning because the certificate is self-signed. This is okay.)

```bash
kubectl get all --namespace akka-cluster-tooling-example

echo "https://$(minikube ip)/cluster-example"
```
