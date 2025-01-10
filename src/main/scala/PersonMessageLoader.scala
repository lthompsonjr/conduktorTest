import config.AppConfig.{bootstrapServers, peopleFileName, topic}
import model.IngestionMessage
import service.{KafkaProducerService, PersonService}
import zio._
import zio.json.DecoderOps

import scala.io.Source

object PersonMessageLoader extends ZIOAppDefault {

  private def readJsonFile: ZIO[Any, Throwable, String] =
    ZIO.acquireReleaseWith(ZIO.attempt(Source.fromResource(peopleFileName)))(source => ZIO.succeed(source.close()))(source => ZIO.attempt(source.getLines().mkString("\n")))

  private val app = for {
    peopleService <- ZIO.service[PersonService]
    content <- readJsonFile
    ingestionMessage <- ZIO.fromEither(content.fromJson[IngestionMessage])
    _ <- peopleService.publishRecords(topic, ingestionMessage)
  } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = app.provide(KafkaProducerService.layer, PersonService.layer)

}
