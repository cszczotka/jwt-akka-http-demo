package com.cszczotka.http
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.server.Directives._
import com.cszczotka.http.api.JwtApi
import com.cszczotka.http.api.handler.CorsHandler
import com.cszczotka.http.streaming.StreamingRoute

object MyHttpServer extends App with JwtApi with CorsHandler {
  val config = ConfigFactory.load()
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  dropTable("login")
  createTableIfNotExists("login")
  createUserIfNotExists("admin", "abc123", "admin")

  val allroutes: Route = {
    JwtApi.routes ~
      StreamingRoute.route
  }

  Http().bindAndHandle(corsHandler(allroutes), config.getString("http.host"), config.getInt("http.port"))
}
