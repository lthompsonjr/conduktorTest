package service

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import zio.macros.accessible
import zio.{ULayer, ZIO, ZLayer}

import java.util.Properties

@accessible
trait KafkaConsumerService {
  def make(bootstrapServers: String, groupId: String): ZIO[Any, Throwable, KafkaConsumer[String,String]]
}


final case class KafkaConsumerServiceLive() extends KafkaConsumerService {
  override def make(bootstrapServers: String, groupId: String): ZIO[Any, Throwable, KafkaConsumer[String, String]] = {
    val properties = new Properties()
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
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