package Model

final case class Player(playerId: String, money: Option[BigDecimal])

import java.util.UUID

final case class Venue(id: Option[UUID], name: String, price: BigDecimal, owner: Option[String])
