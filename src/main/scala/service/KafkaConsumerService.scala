package service

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer

import config.AppConfig
import zio.ULayer
import zio.ZIO
import zio.ZLayer
import zio.macros.accessible

@accessible
trait KafkaConsumerService {
  def make(groupId: String): ZIO[Any, Throwable, KafkaConsumer[String, String]]
}

final case class KafkaConsumerServiceLive() extends KafkaConsumerService {
  override def make(groupId: String): ZIO[Any, Throwable, KafkaConsumer[String, String]] = {
    val properties = new Properties()
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.bootstrapServers)
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    ZIO.attempt(new KafkaConsumer[String, String](properties)).tapError(e => ZIO.logError(e.getMessage))

  }
}

object KafkaConsumerServiceLive {
  val layer: ULayer[KafkaConsumerService] = ZLayer.fromFunction(KafkaConsumerServiceLive.apply _)
}
