package model

import io.scalaland.chimney.dsl.TransformationOps
import zio.json.{DeriveJsonCodec, JsonCodec}

case class Address(street: String, town: String, postode: String) {
  def toView: AddressView = this.into[AddressView].withFieldRenamed(_.postode, _.postcode).transform
}
object Address {
  implicit val addressCodec: JsonCodec[Address] = DeriveJsonCodec.gen[Address]
}