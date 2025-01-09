package model

import zio.json.{DeriveJsonCodec, JsonCodec}

case class PersonView( id: String,
                       name: String,
                       dob: String,
                       address: AddressView,
                       telephone: String,
                       pets: List[String],
                       score: Double,
                       email: String,
                       url: String,
                       description: String,
                       verified: Boolean,
                       salary: Int
                     )

object PersonView {
  implicit val addressViewCodec: JsonCodec[AddressView] = DeriveJsonCodec.gen[AddressView]
  implicit val personViewCodec: JsonCodec[PersonView] = DeriveJsonCodec.gen[PersonView]
}


