package com.cszczotka.http.metrics

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directive0, Directives, RequestContext, Route}
import nl.grons.metrics.scala.DefaultInstrumented

trait StatusCodesMetrics extends DefaultInstrumented with Directives {

  val resp2xx = metrics.counter("status-codes-2xx")
  val resp4xx = metrics.counter("status-codes-4xx")
  val resp5xx = metrics.counter("status-codes-5xx")

  private def m(ctx: RequestContext)(resp: HttpResponse): HttpResponse = {
    if (!ctx.request.uri.path.toString().endsWith("/metrics/prometheus")) {
      resp.status match {
        case _: StatusCodes.Success => resp2xx.inc()
        case _: StatusCodes.ClientError => resp4xx.inc()
        case _: StatusCodes.ServerError => resp5xx.inc()
        case _ => // ignore
      }
    }
    resp
  }

  def statusCodesMetrics(r: Route): Route =  countResponseStatusCodes(r)

  private val countResponseStatusCodes: Directive0 = extractRequestContext.flatMap( context =>
    mapResponse { response =>
      m(context)(response)
    }
  )

}
