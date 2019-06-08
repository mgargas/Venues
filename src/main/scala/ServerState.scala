import java.util.UUID

import Model.{Player, Venue}

import scala.collection.mutable


case class ServerState(venues: mutable.Map[UUID, Venue], players: mutable.Map[String, Player]) {

  def getAllVenues: Iterable[Venue] = venues.values

  def getVenue(id: UUID): Option[Venue] = venues.get(id)

  def saveVenue(venue: Venue): Unit = venues.update(venue.id.get, venue)

  def deleteVenue(id: UUID): Option[Venue] = venues.remove(id)
}
