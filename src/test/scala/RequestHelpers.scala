import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import scala.concurrent.Future
object RequestHelpers {

  private val config = ConfigFactory.load()
  val url = s"http://${config.getString("http.interface")}:${config.getInt("http.port")}"

  def putVenueJson(venueName: String, venuePrice: BigDecimal) = ByteString(
    s"""
       |{"name": "$venueName",
       |"price": $venuePrice}
       """.stripMargin)

  def putVenueRequest(venueId: String, json: ByteString) = HttpRequest(
    HttpMethods.PUT,
    uri = s"$url/venues/$venueId",
    entity = HttpEntity(MediaTypes.`application/json`, json))
}

