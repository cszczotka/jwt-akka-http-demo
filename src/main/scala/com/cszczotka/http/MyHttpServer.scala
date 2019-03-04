package com.cszczotka.http
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.server.Directives._
import com.cszczotka.http.api.JwtApi
import com.cszczotka.http.api.handler.CorsHandler
import com.cszczotka.http.metrics.{MetricsRoute, StatusCodesMetrics}
import com.cszczotka.http.streaming.StreamingRoute
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.dropwizard.DropwizardExports

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn


object MyHttpServer extends App with JwtApi with CorsHandler with StatusCodesMetrics {
  val config = ConfigFactory.load()
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  //make the drop wizard metrics available for the prometheus
  CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricRegistry))


  dropTable("login")
  createTableIfNotExists("login")
  createUserIfNotExists("admin", "abc123", "admin")

  val allRoutes: Route = {
    JwtApi.routes ~
    StreamingRoute.route ~
    MetricsRoute(CollectorRegistry.defaultRegistry)
  }

  val bindingF = Http().bindAndHandle(statusCodesMetrics(corsHandler(allRoutes)), config.getString("http.host"), config.getInt("http.port"))
  println(s"Server is running on port ${config.getInt("http.port")}\nPress RETURN to stop ...")

  sys.addShutdownHook {
    Await.result(
      system.terminate(), 10.seconds
    )
  }

  StdIn.readLine() // let it run until user presses return

  val terminateF = bindingF
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done



}
