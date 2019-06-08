package Model

trait Request
final case class PutVenue(name: String, price: BigDecimal) extends Request
final case class BuyVenue(playerId: String) extends Request