package config

// Not how this should be done however this is rather trivial in scope of the actual task

object AppConfig {

  val defaultNumberOfRecordsToRetrieve = 5
  val bootstrapServers = "localhost:19092"
  val topic = "people"
  val peopleFileName = "random-people-data.json"
  val personConsumerGroupId = "person_consumer"

}
