package Model

import java.util.UUID

final case class Venue(id: Option[UUID], name: String, price: BigDecimal, owner: Option[Player])

