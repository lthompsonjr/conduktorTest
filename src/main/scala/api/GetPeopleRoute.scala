package api

import config.AppConfig.{bootstrapServers, defaultNumberOfRecordsToRetrieve}
import service.{KafkaConsumerService, PersonService}
import zio.ZIO
import zio.http.codec.PathCodec.{int, string}
import zio.http.{Method, Request, Response, Root, Routes, Status, handler}
import zio.json.EncoderOps

object GetPeopleRoute {

  val route: Routes[KafkaConsumerService with PersonService, Response] = Routes(
    Method.GET / Root / "topic" / string("topic") / int("offset") -> handler { (topic: String, offset: Int, req: Request) =>
      val count: Int = req.url.queryParams.getAll("count").headOption.map(_.toInt).getOrElse(defaultNumberOfRecordsToRetrieve)
      if (offset < 0) ZIO.fail(Response.error(Status.BadRequest, "offset value must not greater than or equal to zero"))
      else {
        (for {
          personService <- ZIO.service[PersonService]
          personRecords <- personService.getRecords(bootstrapServers, topic, offset, count)
          _ <- ZIO.logInfo(s"returning ${personRecords.length} records")
          res <- ZIO.succeed(Response.json(personRecords.map(_.toView).toJson))
        } yield res).orElseFail(Response.error(Status.InternalServerError, "failed to retrieve records"))
      }
    }
  )
}
