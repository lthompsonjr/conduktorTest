package model

import io.scalaland.chimney.dsl.TransformationOps
import zio.json.DeriveJsonCodec
import zio.json.JsonCodec

case class PersonMessage(
    _id: String,
    name: String,
    dob: String,
    address: Address,
    telephone: String,
    pets: List[String],
    score: Double,
    email: String,
    url: String,
    description: String,
    verified: Boolean,
    salary: Int
) {

  def toView: PersonView = this
    .into[PersonView]
    .withFieldComputed(_.id, _._id)
    .withFieldComputed(_.address, _.address.toView)
    .transform
}

object PersonMessage {
  implicit val addressCodec: JsonCodec[Address] = DeriveJsonCodec.gen[Address]
  implicit val personMessageCodec: JsonCodec[PersonMessage] = DeriveJsonCodec.gen[PersonMessage]
}
