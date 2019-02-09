package com.cszczotka.http.streaming

import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

object StreamingRoute {
  private val newline = ByteString("\n")

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport
      .json()
      .withFramingRenderer(
      Flow[ByteString].map(byteString => byteString ++ newline)
    )

  def route: Route = get {
    path("stream") {
      complete(DataSource.source)
    }
  }
}
