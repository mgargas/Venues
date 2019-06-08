import java.util.UUID

import Model.{Player, Venue}

import scala.collection.mutable


case class ServerState(venues: mutable.Map[UUID, Venue], players: mutable.Map[String, Player]) {

  def getAllVenues: Iterable[Venue] = venues.values

}
