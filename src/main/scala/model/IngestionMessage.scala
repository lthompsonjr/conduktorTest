package model

import zio.json.{DeriveJsonDecoder, JsonDecoder}

case class IngestionMessage(ctRoot: List[PersonMessage])

object IngestionMessage {
  implicit val addressDecoder: JsonDecoder[Address] = DeriveJsonDecoder.gen[Address]
  implicit val personMessageDecoder: JsonDecoder[PersonMessage] = DeriveJsonDecoder.gen[PersonMessage]
  implicit val ingestionMessageDecoder: JsonDecoder[IngestionMessage] = DeriveJsonDecoder.gen[IngestionMessage]
}