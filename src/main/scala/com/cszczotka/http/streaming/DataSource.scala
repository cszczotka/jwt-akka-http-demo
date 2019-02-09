package com.cszczotka.http.streaming

import akka.NotUsed
import akka.stream.scaladsl.Source

import scala.concurrent.duration._

object DataSource {
  def source: Source[DataChunk, NotUsed] =
    Source(List(
      DataChunk(1, "the first data chunk"),
      DataChunk(2, "the second data chunk"),
      DataChunk(3, "the thrid data chunk"),
      DataChunk(4, "the fourth data chunk"),
      DataChunk(5, "the fifth data chunk"),
      DataChunk(6, "the sixth data chunk"))
    ).throttle(1, 1.second)

}
