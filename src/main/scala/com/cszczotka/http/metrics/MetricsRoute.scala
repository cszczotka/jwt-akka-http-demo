package com.cszczotka.http.metrics

import java.io.StringWriter

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.cszczotka.http.MyHttpServer.system
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

import scala.collection.JavaConverters._
import scala.concurrent.duration._

object MetricsRoute extends MetricDirectives   {

  private val `text/plain; version=0.0.4; charset=utf-8` = MediaType
    .customWithFixedCharset("text", "plain", HttpCharsets.`UTF-8`, params=Map("version" -> "0.0.4"))

  private def renderMetrics(registry: CollectorRegistry, names: Set[String]): String = {
    val writer = new StringWriter()
    TextFormat.write004(writer, registry.filteredMetricFamilySamples(names.toSet.asJava))
    writer.toString
  }


  implicit val executionContext = system.dispatcher

  def aroundRequest = AroundDirectives.aroundRequest(AroundDirectives.timeRequest)


  def apply(registry: CollectorRegistry): Route = {
    get {
      (path("metrics") & parameterMultiMap) { allQueryParams =>
        val nameParams = allQueryParams.getOrElse("name", Nil).toSet
        val content = renderMetrics(registry, nameParams)
        complete {
          HttpResponse(entity = HttpEntity(`text/plain; version=0.0.4; charset=utf-8`, content))
        }
      }

    } ~
    logDuration {
        get {
          path("test") {
            val s = Source.tick(0.seconds, 1.second, "x").take(5).map(ByteString(_))
            complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s))
          }
        }
      } ~
    aroundRequest {
      get {
        path("test2") {
          val s = Source.tick(0.seconds, 1.second, "x").take(5).map(ByteString(_))
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, s))
        }
      }
    }

  }



}
