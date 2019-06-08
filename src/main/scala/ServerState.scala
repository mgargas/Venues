import java.util.UUID

import Model.{Player, Venue}

import scala.collection.mutable

sealed trait BuyResult
sealed case class CantAfford(player: Player, venue: Venue) extends BuyResult {
  override def toString: String = {
    val playerId = player.playerId
    val venueName = venue.name
    s"$playerId can't afford $venueName"
  }
}
sealed case class Bought(player: Player, venue: Venue) extends BuyResult {
  override def toString: String = {
    val playerId = player.playerId
    val venueName = venue.name
    val price = venue.price
    s"$venueName was bought by $playerId for $price"
  }
}
sealed case class AlreadyBought(player: Player, venue: Venue) extends BuyResult {
  override def toString: String = {
    val playerId = player.playerId
    val venueName = venue.name
    val price = venue.price
    s"$venueName has been already bought by $playerId for $price"
  }
}

case class ServerState(venues: mutable.Map[UUID, Venue], players: mutable.Map[String, Player]) {

  def getAllVenues: Iterable[Venue] = venues.values

  def getVenue(id: UUID): Option[Venue] = venues.get(id)

  def saveVenue(venue: Venue): Unit = venues.update(venue.id.get, venue)

  def deleteVenue(id: UUID): Option[Venue] = venues.remove(id)

  def buyVenue(venueId: UUID, playerId: String): Option[BuyResult] = {
    venues.get(venueId)
      .flatMap(venue => {
        val ownerOption = venue.owner
        ownerOption match {
          case Some(currentOwner) => Some(AlreadyBought(currentOwner, venue))
          case None =>
            players.get(playerId).map(player => {
              val playerMoney = player.money.getOrElse(BigDecimal(0))
              val venuePrice = venue.price
              if (playerMoney >= venuePrice) {
                venues.update(venueId, venue.copy(owner = Some(player)))
                players.update(playerId, player.copy(money = Some(playerMoney - venuePrice)))
                Bought(player, venue)
              } else {
                CantAfford(player, venue)
              }
            })
        }
      })
  }
}
