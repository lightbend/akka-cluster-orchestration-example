# Akka Cluster Tooling Example

This project is an example for [Lightbend Reactive Platform Tooling](https://developer.lightbend.com/docs/reactive-platform-tooling/latest/).

This is a very simple [Akka Cluster](https://doc.akka.io/docs/akka/snapshot/cluster-usage.html) and 
[Akka HTTP](https://doc.akka.io/docs/akka-http/current/scala/http/) application. Its main purpose is to show how you 
can build a similar application and take advantage of the [Platform Tooling](https://s3-us-west-2.amazonaws.com/rp-tooling-temp-docs/home.html)
to easily and safely deploy to [Kubernetes](https://kubernetes.io/) with minimal configuration required.

### Deployment

Prerequisites:

* [reactive-cli](https://developer.lightbend.com/docs/reactive-platform-tooling/latest/cli-installation.html)
* [Minikube](https://github.com/kubernetes/minikube#installation)

> **You'll need version 0.8.0 (or later) of the CLI.**

> If you already have a Minikube and wish to start fresh, run `minikube delete` to remove your existing Minikube.

1) Start minikube

```bash
minikube start

eval $(minikube docker-env)

minikube addons enable ingress
```

2) Build and Deploy project

There's two ways to deploy this project. You can do it from within SBT or from the command line.

### SBT

This is the developer friendly method of running the project. Simply invoke the `deploy minikube` task.

```bash
sbt 'deploy minikube'
```

### Command Line

This matches more closely what you'd do in a production environment. You'll build the project. Then, you'll use `rp`
to generate resources for it and pipe those to `kubectl`.

#### Build project

```bash
sbt docker:publishLocal
```

#### Inspect images

```bash
docker images
```

#### Deploy project

```bash
rp generate-kubernetes-resources akka-cluster-tooling-example:0.1.0 \
  --generate-all \
  --ingress-annotation ingress.kubernetes.io/rewrite-target=/ \
  --ingress-annotation nginx.ingress.kubernetes.io/rewrite-target=/ \
  --pod-controller-replicas 3 | kubectl apply -f -
```

3) View Results (Note: `-k` is needed because the certificate is self-signed)

This may take 30 seconds or so until all pods have been started and the cluster has formed. You should see a member
listing.

```bash
kubectl get all
```

```
NAME                                         DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
deploy/akka-cluster-tooling-example-v0-1-0   3         3         3            3           1m

NAME                                                DESIRED   CURRENT   READY     AGE
rs/akka-cluster-tooling-example-v0-1-0-6d8b954c45   3         3         3         1m

NAME                                         DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
deploy/akka-cluster-tooling-example-v0-1-0   3         3         3            3           1m

NAME                                                DESIRED   CURRENT   READY     AGE
rs/akka-cluster-tooling-example-v0-1-0-6d8b954c45   3         3         3         1m

NAME                                                      READY     STATUS    RESTARTS   AGE
po/akka-cluster-tooling-example-v0-1-0-6d8b954c45-2jzbb   1/1       Running   0          1m
po/akka-cluster-tooling-example-v0-1-0-6d8b954c45-vfkg6   1/1       Running   0          1m
po/akka-cluster-tooling-example-v0-1-0-6d8b954c45-zq8wj   1/1       Running   0          1m

NAME                               TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)                         AGE
svc/akka-cluster-tooling-example   ClusterIP   None         <none>        10000/TCP,10001/TCP,10002/TCP   1m
svc/kubernetes                     ClusterIP   10.96.0.1    <none>        443/TCP                         3m
```

```bash
curl -k "https://$(minikube ip)/cluster-example"
```

```
Akka Cluster Members
====================

akka.tcp://my-system@172.17.0.8:10000
akka.tcp://my-system@172.17.0.7:10000
akka.tcp://my-system@172.17.0.6:10000
```

4) Run the job (Optional)

This application has a job in it that joins the existing cluster, prints a message, and then exits.

```bash
rp generate-kubernetes-resources akka-cluster-tooling-example:0.1.0 \
  --application my-job \
  --akka-cluster-join-existing \
  --generate-pod-controllers \
  --pod-controller-type job | kubectl apply -f -
```

You can then inspect the jobs output using:

```bash
kubectl logs jobs/akka-cluster-tooling-example-v0-1-0
```

And lastly, remove the job, thus deleting logs:

```bash
kubectl delete jobs/akka-cluster-tooling-example-v0-1-0
```

5) Remove project (Optional)

If you wish to remove the resources, you can use `kubectl delete` as follows:

```bash
rp generate-kubernetes-resources akka-cluster-tooling-example/akka-cluster-tooling-example:0.1.0 \
  --pod-controller-replicas 3 | kubectl delete -f -
```

