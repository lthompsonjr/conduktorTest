package service

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import org.apache.kafka.common.serialization.StringSerializer
import zio.macros.accessible
import zio.{IO, ULayer, ZIO, ZLayer}

import java.util.Properties

@accessible
trait KafkaProducerService {
  def make(bootstrapServers: String): ZIO[Any, Throwable, KafkaProducer[String,String]]
}

final case class KafkaProducerServiceLive() extends KafkaProducerService {
  override def make(bootstrapServers: String): IO[Throwable, KafkaProducer[String, String]] = {
       val properties = new Properties()
       properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
       properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
       properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)

    ZIO.attempt(new KafkaProducer[String, String](properties))
  }
}


object KafkaProducerService {
  val layer: ULayer[KafkaProducerService] = ZLayer.fromFunction(KafkaProducerServiceLive.apply _)
}