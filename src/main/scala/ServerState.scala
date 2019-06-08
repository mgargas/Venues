import java.util.UUID

import Model.{Player, Venue}

import scala.collection.mutable

sealed trait BuyResult
sealed case class CantAfford(playerId: String, venue: Venue) extends BuyResult {
  override def toString: String = {
    val venueName = venue.name
    s"$playerId can't afford $venueName"
  }
}
sealed case class Bought(playerId: String, venue: Venue) extends BuyResult {
  override def toString: String = {
    val venueName = venue.name
    val price = venue.price
    s"$venueName was bought by $playerId for $price"
  }
}
sealed case class AlreadyBought(playerId: String, venue: Venue) extends BuyResult {
  override def toString: String = {
    val venueName = venue.name
    val price = venue.price
    s"$venueName has been already bought by $playerId for $price"
  }
}

case class ServerState(venues: mutable.Map[UUID, Venue], players: mutable.Map[String, Player]) {

  def getAllVenues: Iterable[Venue] = venues.values

  def getVenue(id: UUID): Option[Venue] = venues.get(id)

  def saveVenue(venueToSave: Venue): Unit = {
    val id = venueToSave.id.get
    venues.get(id) match {
      case Some(venue) if venue.owner.isEmpty => venues.update(id, venueToSave)
      case None => venues.update(id, venueToSave)
      case _ =>
        println("jaja")
        Unit
    }
  }

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
                venues.update(venueId, venue.copy(owner = Some(playerId)))
                players.update(playerId, player.copy(money = Some(playerMoney - venuePrice)))
                Bought(playerId, venue)
              } else {
                CantAfford(playerId, venue)
              }
            })
        }
      })
  }
}
