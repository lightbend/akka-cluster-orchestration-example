package com.lightbend.example.com.lightbend.rp.example.akkacluster

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.duration._

import SimpleClusterListener._

object App {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    implicit val timeout = Timeout(1.second)

    val clusterListener = system.actorOf(Props(new SimpleClusterListener))

    // Simple Akka HTTP route that responds with this nodes view of cluster membership

    val route =
      path("ping") {
        get(complete("pong!"))
      } ~
      pathEndOrSingleSlash {
        complete {
          (clusterListener ? SimpleClusterListener.GetMembers)
            .mapTo[MemberList]
            .map(template)
        }
      }

    // Use `SocketBinding` to inspect environment for correct host/port to bind on
    // or fall back to a default provided value

    val host = "0.0.0.0"
    val port = 8080

    println(s"HTTP server available at http://$host:$port")

    Http().bindAndHandle(route, host, port)
  }

  private def template(members: MemberList): String =
    s"""|Akka Cluster Members
        |====================
        |
        |${members.members.mkString("\n")}""".stripMargin
}
