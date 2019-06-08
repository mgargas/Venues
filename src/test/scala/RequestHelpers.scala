import akka.http.scaladsl.model._
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

object RequestHelpers {

  private val config = ConfigFactory.load()
  private val url = s"http://${config.getString("http.interface")}:${config.getInt("http.port")}"

  def putVenueJson(venueName: String, venuePrice: BigDecimal) = ByteString(
    s"""
       |{"name": "$venueName",
       |"price": $venuePrice}
       """.stripMargin)

  def postBuyVenueJson(playerId: String) = ByteString(
    s"""
       |{"playerId": "$playerId"}
       """.stripMargin)

  def putVenueRequest(venueId: String, json: ByteString) = HttpRequest(
    HttpMethods.PUT,
    uri = s"$url/venues/$venueId",
    entity = HttpEntity(MediaTypes.`application/json`, json))

  def postBuyVenueRequest(venueId: String, json: ByteString) = HttpRequest(
    HttpMethods.POST,
    uri = s"$url/venues/$venueId/buy",
    entity = HttpEntity(MediaTypes.`application/json`, json)
  )
}

