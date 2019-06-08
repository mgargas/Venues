import akka.http.scaladsl.model._
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

object RequestHelpers {

  val url = s"http://${config.getString("http.interface")}:${config.getInt("http.port")}"
  private val config = ConfigFactory.load()

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

