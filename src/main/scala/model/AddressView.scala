package model

import zio.json.DeriveJsonCodec
import zio.json.JsonCodec

case class AddressView(street: String, town: String, postcode: String)
object AddressView {
  implicit val addressCodec: JsonCodec[AddressView] = DeriveJsonCodec.gen[AddressView]
}
