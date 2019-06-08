import java.util.UUID
import java.util.logging.{Level, Logger}

import Model.{Player, Venue}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}
import akka.event.Logging
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

}

object WebServer extends JsonSupport {
  private implicit val system: ActorSystem = ActorSystem()
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  private val serverState = ServerState(
    mutable.Map.empty[UUID, Venue],
    mutable.Map("player1" -> Player("player1", Some(500)), "player2" -> Player("player2", Some(2000))))
  val log = Logging(system, "webServer")
  val mainRoute: Route =
    path("venues") {
      get {
        complete(serverState.getAllVenues)
      }
    }

  def main(args: Array[String]) {

    val config = ConfigFactory.load()
    Http().bindAndHandle(mainRoute, config.getString("http.interface"), config.getInt("http.port"))
    log.info(s"Running on port ${config.getInt("http.port")}...")
  }
}