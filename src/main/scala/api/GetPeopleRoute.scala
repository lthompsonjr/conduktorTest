package api

import config.AppConfig.bootstrapServers
import config.AppConfig.defaultNumberOfRecordsToRetrieve
import service.KafkaConsumerService
import service.PersonService
import zio.ZIO
import zio.http.Method
import zio.http.Request
import zio.http.Response
import zio.http.Root
import zio.http.Routes
import zio.http.Status
import zio.http.codec.PathCodec.int
import zio.http.codec.PathCodec.string
import zio.http.handler
import zio.json.EncoderOps

object GetPeopleRoute {

  val route: Routes[KafkaConsumerService with PersonService, Response] =
    Routes {
      Method.GET / Root / "topic" / string("topic") / int("offset") -> handler { (topic: String, offset: Int, req: Request) =>
        val count: Int = req.url.queryParams
          .getAll("count")
          .headOption
          .map(_.toInt)
          .getOrElse(defaultNumberOfRecordsToRetrieve)
        if (offset < 0)
          ZIO.fail(
            Response.error(
              Status.BadRequest,
              "offset value must not greater than or equal to zero"
            )
          )
        else
          (for {
            personService <- ZIO.service[PersonService]
            personRecords <- personService.getRecords(
              bootstrapServers,
              topic,
              offset,
              count
            )
            _ <- ZIO.logInfo(s"returning ${personRecords.length} records")
            res <- ZIO.succeed(
              Response.json(personRecords.map(_.toView).toJson)
            )
          } yield res).orElseFail(
            Response.error(
              Status.InternalServerError,
              "failed to retrieve records"
            )
          )
      }
    }
}
