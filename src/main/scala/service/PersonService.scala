package service

import config.AppConfig.personConsumerGroupId
import model.{IngestionMessage, PersonMessage}
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import zio.json.{DecoderOps, EncoderOps}
import zio.macros.accessible
import zio.stream.ZStream
import zio.{Ref, ULayer, ZIO, ZLayer}

import java.time.Duration
import scala.jdk.CollectionConverters.{IterableHasAsJava, IterableHasAsScala}

@accessible
  trait PersonService {
  def getRecords(bootstrapServers: String, topic: String, offset: Int, count: Int): ZIO[KafkaConsumerService, Serializable, List[PersonMessage            ]]
  def publishRecords(bootstrapServers: String, topic: String, ingestionMessage: IngestionMessage): ZIO[KafkaProducerService, Throwable, Unit]
}

final case class PersonServiceLive() extends PersonService {
  override def getRecords(bootstrapServers: String, topic: String, offset: Int, count: Int): ZIO[KafkaConsumerService, Serializable, List[PersonMessage]] = {

    val p1 = new TopicPartition(topic, 0)
    val p2 = new TopicPartition(topic, 1)
    val p3 = new TopicPartition(topic, 2)

    def assignAndSeek(consumer: KafkaConsumer[String, String]) =
      ZIO.attempt {
        consumer.assign(List(p1, p2, p3).asJavaCollection)
        consumer.seek(p1, offset)
        consumer.seek(p2, offset)
        consumer.seek(p3, offset)
      }

    def pollRecords(consumer: KafkaConsumer[String, String]): ZStream[Any, Throwable, ConsumerRecord[String, String]] =
      ZStream.fromIterableZIO {
        ZIO.attempt {
          consumer.poll(Duration.ofMillis(1000))
        }.map(_.records(topic).asScala.toList)
      }

    ZIO.acquireReleaseWith {
      for {
        consumerService <- ZIO.service[KafkaConsumerService]
        _ <- ZIO.logInfo("creating consumer")
        consumer <- consumerService.make(bootstrapServers, personConsumerGroupId)
        _ <- ZIO.logInfo("consumer created")
      } yield consumer
    }(consumer => (ZIO.attempt(consumer.close()) *> ZIO.logInfo("closing consumer")).orDie) { consumer =>
      for {
        _ <- assignAndSeek(consumer)
        _ <- ZIO.logInfo("attempting to consume records")
        records <- pollRecords(consumer).runCollect
        _ <- ZIO.foreachDiscard(records)(record => ZIO.log(s"Consumed record - id: ${record.key()}, partition: ${record.partition()}, offset: ${record.offset()}"))
        _ <- ZIO.logInfo(s"consumed ${records.length} records")
        personRecords <- ZIO.foreach(records.toList.take(count).map(record => record.value().fromJson[PersonMessage]))(maybePersonRecord =>
          ZIO.fromEither(maybePersonRecord)
        )
      } yield personRecords
    }
  }

  override def publishRecords(bootstrapServers: String, topic: String, ingestionMessage: IngestionMessage): ZIO[KafkaProducerService, Throwable, Unit] = {
    for {
      producerService <- ZIO.service[KafkaProducerService]
      producer <- producerService.make(bootstrapServers)
      records = ingestionMessage.ctRoot.map(message =>
        new ProducerRecord[String, String](topic, message._id, message.toJson)
      )
      count <- Ref.make(0)
      _ <- ZIO.foreachDiscard(records) { record =>
        count.update(_ + 1) *>
          ZIO.log(s"attempting to send record $record") *>
          ZIO.attempt(producer.send(record)).tapError(e => ZIO.logError(e.getMessage)) *>
          count.get.flatMap(c => ZIO.log(s"sent $c records successfully"))
      }
    } yield ()
  }
}



object PersonService {
  val layer: ULayer[PersonService] = ZLayer.fromFunction(PersonServiceLive.apply _)
}