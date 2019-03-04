package com.cszczotka.http.metrics

import akka.http.scaladsl.server.{Directive0, Directives}


trait MetricDirectives extends Directives  {

  /**
    * we only observe when the HttpResponse instance is created, not when the response has been sent!
    */
  val logDuration: Directive0 = extractRequestContext.flatMap { ctx =>
    val start = System.currentTimeMillis()
    mapResponse { resp =>
      val d = System.currentTimeMillis() - start
      println(s"[${resp.status.intValue()}] ${ctx.request.method.name} " +
        s"${ctx.request.uri} took: ${d}ms")
      resp
    }
  }

}
