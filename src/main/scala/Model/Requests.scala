package Model

trait Request
final case class PutVenue(name: String, price: BigDecimal) extends Request {
  require(name.nonEmpty, "Name of the venue can not be empty")
  require(price > 0, "Price of the venue must be greater than 0")
}
final case class BuyVenue(playerId: String) extends Request {
  require(playerId.nonEmpty, "Id of the player can not be empty")
}