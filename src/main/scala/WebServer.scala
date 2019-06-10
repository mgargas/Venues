import java.util.UUID

import Model.{BuyVenue, Player, PutVenue, Venue}
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{post, _}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}

import scala.collection.mutable
import scala.concurrent.{ExecutionContextExecutor, Future}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val playerFormat: RootJsonFormat[Player] = jsonFormat2(Player)

  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID) = JsString(uuid.toString)

    def read(value: JsValue): UUID = {
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _ => throw DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

  implicit val venueFormat: RootJsonFormat[Venue] = jsonFormat4(Venue)
  implicit val putVenueFormat: RootJsonFormat[PutVenue] = jsonFormat2(PutVenue)
  implicit val buyVenueFormat: RootJsonFormat[BuyVenue] = jsonFormat1(BuyVenue)

}

object WebServer extends JsonSupport {
  private implicit val system: ActorSystem = ActorSystem()
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  private val logger = Logging.getLogger(system, this)
  private val serverState = ServerState(
    mutable.Map.empty[UUID, Venue],
    mutable.Map("player1" -> Player("player1", Some(500)), "player2" -> Player("player2", Some(2000))))

  val mainRoute: Route =
    pathPrefix("venues" / JavaUUID) { id => venueRoute(id) } ~
      path("venues") {
        get {
          logger.info("Received GET request for all venues")
          complete(serverState.getAllVenues)
        }
      }

  def main(args: Array[String]) {
    val config = ConfigFactory.load()
    Http().bindAndHandle(mainRoute, config.getString("http.interface"), config.getInt("http.port"))
    logger.info(s"Running on port ${config.getInt("http.port")}...")
  }

  private def venueRoute(id: UUID): Route =
    concat(
      get {
        logger.info("Received GET request for venue " + id)
        val maybeVenue: Future[Option[Venue]] = Future {
          serverState.getVenue(id)
        }
        onSuccess(maybeVenue) {
          case Some(venue) => complete(venue)
          case None => complete(StatusCodes.NotFound)
        }
      },
      put {
        logger.info("Received PUT request for venue " + id)
        entity(as[PutVenue]) { putVenue =>
          val saved: Future[Unit] = Future {
            serverState.saveVenue(Venue(id = id, name = putVenue.name, price = putVenue.price, owner = None))
          }
          onSuccess(saved)(complete(id.toString))
        }
      },
      delete {
        logger.info("Received DELETE request for venue " + id)
        val deleted = Future {
          serverState.deleteVenue(id)
        }
        onSuccess(deleted) {
          case Some(_) => complete(id.toString)
          case None => complete(StatusCodes.NotFound)
        }
      },
      post {
        path("buy") {
          logger.info("Received POST request for venue " + id)
          entity(as[BuyVenue]) { buyVenue =>
            val playerId = buyVenue.playerId
            logger.info(s"Player $playerId wants to buy venue " + id)
            val maybeBuyResult = Future {
              serverState.buyVenue(id, playerId)
            }
            onSuccess(maybeBuyResult) {
              case Some(buyResult) =>
                complete(buyResult.toString)
              case None => complete(StatusCodes.NotFound)
            }
          }
        }
      }
    )
}