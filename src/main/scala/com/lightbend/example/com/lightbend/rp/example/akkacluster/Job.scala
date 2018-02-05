package com.lightbend.example.com.lightbend.rp.example.akkacluster

import akka.actor.ActorSystem
import akka.cluster.Cluster

object Job {
  def main(args: Array[String]) {
    val system = ActorSystem("my-system")
    val cluster = Cluster(system)

    cluster.registerOnMemberUp {
      println("Doing some work!")

      cluster.leave(cluster.selfAddress)
    }

    cluster.registerOnMemberRemoved {
      println("Work is done!")

      system.terminate()
    }
  }
}